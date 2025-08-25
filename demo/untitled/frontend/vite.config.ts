// vite.config.js
import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'
import fs from 'fs'
import path from 'path'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    cors: true,
    https: {
      key: fs.readFileSync(path.resolve(__dirname, 'localhost-key.pem')),
      cert: fs.readFileSync(path.resolve(__dirname, 'localhost.pem'))
    },
    hmr: {
      protocol: 'https',
      host: 'it-laptop-2',
      port: 5173,
    },
    proxy: {
      '/api': {
        target: 'https://it-laptop-2:5173',
        changeOrigin: true,
        secure: true,
        ws: true,
      }
    },

  }
})