# LB Cifras

App Android offline-first para biblioteca e leitura de cifras.

## Stack

- Kotlin
- Jetpack Compose
- Room (SQLite)
- MVVM + Repository + UseCases

## O que ja esta pronto

- Estrutura Android com Gradle Kotlin DSL
- Persistencia local com Room
- Biblioteca de cifras com busca por titulo/artista
- Cadastro rapido de cifra em formato ChordPro
- Tela de leitura dedicada com fonte monoespacada
- Destaque visual de acordes em ChordPro
- Auto-scroll com play/pause e controle de velocidade
- Edicao e exclusao de cifras
- Importacao por link (Cifra Club e Ultimate Guitar)

## Estrutura principal

- app/src/main/java/br/com/lbcifras/data
- app/src/main/java/br/com/lbcifras/domain
- app/src/main/java/br/com/lbcifras/presentation

## Como abrir e rodar

1. Abra o projeto no Android Studio.
2. Aguarde a sincronizacao do Gradle.
3. Execute no emulador/dispositivo com Android 7.0+.

Observacao: o Gradle Wrapper nao foi gerado automaticamente porque o comando `gradle` nao esta disponivel no ambiente de terminal atual.
No Android Studio, voce pode sincronizar normalmente e depois gerar wrapper com a acao de Gradle quando necessario.

## Proximas entregas sugeridas

1. Importacao de arquivo .txt.
2. Playlists (repertorios).
3. Parser ChordPro com layout acorde/silaba em linhas separadas.
4. Transposicao de tom.
