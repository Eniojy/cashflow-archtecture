// Configuração do docToolchain para a documentação arc42 deste projeto.

outputPath = 'build/docs'
inputPath = 'arc42'

inputFiles = [
    [file: 'README.adoc', formats: ['html', 'pdf']],
]

// Recursos locais do documento. O HTML também os incorpora por data-uri.
imageDirs = [
    'images/.',
]

failOnMissingImages = true

taskInputsDirs = [
    inputPath,
]

taskInputsFiles = []

