import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import mkcert from 'vite-plugin-mkcert'

export default defineConfig({
    plugins: [
        react(),
        mkcert()
    ],
    server: {
        https: true,
        host: true,
        proxy: {
            '/api': {
                target: 'https://172.19.148.99:8443',
                changeOrigin: true,
                secure: false
            }
        }
    }
})