import { cpSync, existsSync, mkdirSync, rmSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const frontendDirectory = dirname(dirname(fileURLToPath(import.meta.url)));
const webSource = join(frontendDirectory, 'src', 'main', 'webapp');
const outputDirectory = join(frontendDirectory, 'dist');
const bootstrapPackage = join(frontendDirectory, 'node_modules', 'bootstrap');
const bootstrapOutput = join(outputDirectory, 'resources', 'vendor');

if (existsSync(outputDirectory)) {
  rmSync(outputDirectory, { recursive: true });
}
mkdirSync(outputDirectory, { recursive: true });
mkdirSync(bootstrapOutput, { recursive: true });
cpSync(webSource, outputDirectory, { recursive: true });
cpSync(join(bootstrapPackage, 'dist', 'css', 'bootstrap.min.css'),
  join(bootstrapOutput, 'bootstrap.min.css'));
cpSync(join(bootstrapPackage, 'LICENSE'), join(bootstrapOutput, 'BOOTSTRAP-LICENSE'));
