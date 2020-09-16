import nodeResolve from '@rollup/plugin-node-resolve';

export default {
  input: 'dist/esm/index.js',
  output: {
    file: 'dist/plugin.js',
    format: 'iife',
    name: 'capacitorPlugin',
    globals: {
      '@capacitor/core': 'capacitorExports',
      //      'hls.js': 'Hls',
    },
    sourcemap: true,
  },
  plugins: [
    nodeResolve({
      // allowlist of dependencies to bundle in
      // @see https://github.com/rollup/plugins/tree/master/packages/node-resolve#resolveonly
      resolveOnly: ['lodash'],
    }),
  ],
};
