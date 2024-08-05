// tailwind.config.js
module.exports = {
    content: [
        './src/main/resources/templates/**/*.{html,js}', // Thymeleaf 템플릿 경로
        './src/main/resources/static/**/*.{html,js}', // Static 파일 경로
    ],
    theme: {
        extend: {},
    },
    plugins: [],
}
