import { createRequire } from 'node:module';
import { mkdir, readdir, readFile, unlink, writeFile } from 'node:fs/promises';
import { join, resolve } from 'node:path';

const require = createRequire(import.meta.url);
const puppeteer = require('puppeteer');

const [radDirectory, sddDirectory, outputDirectory] = process.argv.slice(2).map((path) => resolve(path));
if (!radDirectory || !sddDirectory || !outputDirectory) {
  throw new Error('Usage: render-word-images.mjs <rad-png-dir> <sdd-png-dir> <output-dir>');
}

const PPI = 300;
const WORD_CONTENT_WIDTH = Math.round((160 / 25.4) * PPI);
const A4_QUARTER_AREA = (210 * 297) / 4;
const CANVAS_16_9 = { width: WORD_CONTENT_WIDTH, height: Math.round(WORD_CONTENT_WIDTH * 9 / 16) };
const FOUR_THREE_WIDTH_MM = Math.sqrt(A4_QUARTER_AREA * 4 / 3);
const CANVAS_4_3 = {
  width: Math.round((FOUR_THREE_WIDTH_MM / 25.4) * PPI),
  height: Math.round((FOUR_THREE_WIDTH_MM / 25.4) * PPI * 3 / 4),
};

const crcTable = new Uint32Array(256);
for (let n = 0; n < 256; n += 1) {
  let c = n;
  for (let k = 0; k < 8; k += 1) c = (c & 1) ? (0xedb88320 ^ (c >>> 1)) : (c >>> 1);
  crcTable[n] = c >>> 0;
}

function crc32(buffer) {
  let crc = 0xffffffff;
  for (const value of buffer) crc = crcTable[(crc ^ value) & 0xff] ^ (crc >>> 8);
  return (crc ^ 0xffffffff) >>> 0;
}

function addPrintResolution(png) {
  const signature = png.subarray(0, 8);
  const chunks = [];
  let offset = 8;
  while (offset < png.length) {
    const length = png.readUInt32BE(offset);
    const type = png.toString('ascii', offset + 4, offset + 8);
    const end = offset + 12 + length;
    if (type !== 'pHYs') chunks.push(png.subarray(offset, end));
    offset = end;
  }

  const data = Buffer.alloc(9);
  const pixelsPerMetre = Math.round(PPI / 0.0254);
  data.writeUInt32BE(pixelsPerMetre, 0);
  data.writeUInt32BE(pixelsPerMetre, 4);
  data.writeUInt8(1, 8);

  const type = Buffer.from('pHYs');
  const crcInput = Buffer.concat([type, data]);
  const chunk = Buffer.alloc(4 + crcInput.length + 4);
  chunk.writeUInt32BE(data.length, 0);
  crcInput.copy(chunk, 4);
  chunk.writeUInt32BE(crc32(crcInput), 4 + crcInput.length);

  return Buffer.concat([signature, chunks[0], chunk, ...chunks.slice(1)]);
}

function pngDimensions(png, filename) {
  if (png.toString('ascii', 1, 4) !== 'PNG' || png.toString('ascii', 12, 16) !== 'IHDR') {
    throw new Error(`Invalid PNG file: ${filename}`);
  }
  return { width: png.readUInt32BE(16), height: png.readUInt32BE(20) };
}

const browser = await puppeteer.launch({ headless: true, args: ['--no-sandbox', '--disable-dev-shm-usage'] });
try {
  await mkdir(outputDirectory, { recursive: true });
  for (const name of await readdir(outputDirectory)) {
    if (/^(?:rad|sdd)(?:-rendered-)?\d+.*\.png$/.test(name)) await unlink(join(outputDirectory, name));
  }

  for (const [tag, directory] of [['rad', radDirectory], ['sdd', sddDirectory]]) {
    const files = (await readdir(directory)).filter((name) => name.endsWith('.png')).sort((a, b) => {
      const ai = Number(a.match(/-(\d+)\.png$/)?.[1] ?? 0);
      const bi = Number(b.match(/-(\d+)\.png$/)?.[1] ?? 0);
      return ai - bi;
    });
    if (files.length === 0) throw new Error(`No Mermaid PNG files found in ${directory}`);

    for (let index = 0; index < files.length; index += 1) {
      const filename = files[index];
      const png = await readFile(join(directory, filename));
      const { width: sourceWidth, height: sourceHeight } = pngDimensions(png, filename);
      const aspectRatio = sourceWidth / sourceHeight;
      const pageSize = aspectRatio >= 1.2 ? CANVAS_16_9 : CANVAS_4_3;
      const imageData = `data:image/png;base64,${png.toString('base64')}`;
      const page = await browser.newPage();
      const html = `<!doctype html><html><head><meta charset="utf-8"><style>
        * { box-sizing: border-box; }
        html, body { margin: 0; width: ${pageSize.width}px; height: ${pageSize.height}px; background: #fff; }
        img { display: block; width: 100%; height: 100%; object-fit: contain; }
      </style></head><body><img src="${imageData}" alt=""></body></html>`;

      await page.setViewport({ ...pageSize, deviceScaleFactor: 1 });
      await page.setContent(html, { waitUntil: 'load' });
      await page.waitForFunction(() => document.images.length === 1 && document.images[0].complete && document.images[0].naturalWidth > 0);
      const rendered = await page.screenshot({ type: 'png' });
      const outName = `${tag}${index + 1}.png`;
      await writeFile(join(outputDirectory, outName), addPrintResolution(rendered));
      await page.close();
    }
  }
} finally {
  await browser.close();
}
