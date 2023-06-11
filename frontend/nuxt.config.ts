export default defineNuxtConfig({
    app: {
        head: {
            title: 'SignalApp',
            link: [
                { rel: 'icon', type: 'image/svg', href: '/favicon.svg' }
            ],
        },
    },
    ssr: false,
    css: ['vuetify/lib/styles/main.sass'],
    build: {
        transpile: ['vuetify']
    },
    vite: {
        define: {
            'process.env.DEBUG': 'false',
            'global': {},
        }
    },
    modules: [
        '@pinia/nuxt',
    ],
})
