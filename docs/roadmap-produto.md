Updated todo list

**Fase 1: Igualar e superar o básico dos concorrentes**
Primeiro, funcionalidades que usuários já esperam em apps similares.

1. Leitura e performance
- Tamanho da fonte da letra e do acorde separados.
- Espaçamento entre linhas ajustável.
- Modo escuro de palco com alto contraste.
- Trava de rotação e manter tela ligada no modo leitura.
- Rolagem com slider contínuo de velocidade (além do + e -).

2. Cifra e música
- Transposição de tom com 1 toque (+1, -1, reset).
- Capo virtual (mostrar acorde “real” e “forma”).
- Destaque opcional de acordes (on/off).
- Marcadores de seção (Verso, Refrão, Ponte).

3. Biblioteca
- Favoritos.
- Tags (louvor, rock, acústico, show X).
- Ordenação por título, artista, atualização e mais tocadas.
- Histórico de recentes.

4. Importação e dados
- Importar por link mais robusto.
- Importar arquivo txt/html local.
- Exportar/importar backup (json/zip).
- Detecção de duplicatas ao importar.

5. Repertório (setlist)
- Criar repertórios.
- Reordenar músicas por drag and drop.
- Próxima/anterior no repertório sem voltar para biblioteca.
- Modo apresentação contínua.

**Fase 2: Diferenciar de verdade (o que concorrente geralmente não faz bem)**
Aqui entra o “algo melhor”.

1. Rolagem inteligente por seção
- Velocidade diferente por trecho (verso mais lento, refrão mais rápido).
- Botão de “pular para próxima seção”.

2. Assistente de ensaio
- Cronômetro por música.
- Nota de palco por música (ex: entrada bateria, parada, dinâmica).
- Contagem de compassos para início.

3. Sistema de arranjos por música
- Uma música com múltiplas versões:
  - versão voz e violão
  - versão banda completa
  - versão tom feminino
- Troca rápida entre versões.

4. Qualidade de cifra
- Validador de cifra: detecta acordes estranhos/inconsistentes.
- Limpeza automática de texto importado (ruído, ads, metadados).

5. Offline-first de verdade com sync opcional
- Funciona tudo offline.
- Sincronização opcional com nuvem só para backup e compartilhamento.

**Roadmap recomendado (ordem prática)**
1. Sprint 1 (rápido e muito valor): favoritos, slider de rolagem, tamanho de fonte, manter tela ligada.
2. Sprint 2 (núcleo musical): transposição, capo, repertório com ordenação.
3. Sprint 3 (diferenciação): rolagem por seção, notas de palco, versões por música.
4. Sprint 4 (produto forte): backup/sync e importador avançado.

**Como tornar melhor que concorrente em uma frase**
Focar em “fluxo de músico ao vivo”: abrir rápido, achar rápido, tocar sem fricção e sem tocar no celular durante a música.

Se quiser, eu já parto para implementação agora em ordem de maior impacto. Sugestão de começar por:
1. Slider de velocidade + controle de fonte na tela de cifra.
2. Favoritos e filtro de favoritos.
3. Repertório com próxima/anterior dentro da tela de leitura.