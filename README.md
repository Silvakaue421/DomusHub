# DomusHub — Gestão inteligente da despensa

Sistema web acadêmico desenvolvido em Java para organizar produtos de uma despensa doméstica, acompanhar quantidades, registrar lotes e datas de validade e alertar sobre estoque baixo e vencimentos próximos.

## 1. Tecnologias
- Java 21
- Spring Boot 3.4.5
- Spring MVC + Thymeleaf
- Spring Security (autenticação e autorização por perfil)
- Spring Data JPA / Hibernate
- PostgreSQL 16
- Maven

## 2. Requisitos para executar
- JDK 21 instalado
- Maven 3.9+
- Docker Desktop (recomendado) ou PostgreSQL 16 instalado

Confira:
```bash
java -version
mvn -version
docker --version
```

## 3. Como executar
Na pasta raiz do projeto:

1. Inicie o banco:
```bash
docker compose up -d
```
2. Inicie a aplicação:
```bash
mvn spring-boot:run
```
3. Abra no navegador: http://localhost:8080

Para encerrar o banco:
```bash
docker compose down
```
Os dados ficam no volume Docker `domushub_data`. Para apagar o banco de demonstração e seus dados:
```bash
docker compose down -v
```

## 4. Usuários iniciais de demonstração
O sistema cria estes usuários na primeira inicialização, caso ainda não existam:

| Perfil | E-mail | Senha |
|---|---|---|
| Administrador | admin@domushub.com | Domus@123 |
| Morador | morador@domushub.com | Morador@123 |

**Atenção:** credenciais são apenas para demonstração acadêmica. Altere-as antes de qualquer implantação real. Não use essas senhas em serviços pessoais.

## 5. Funcionalidades
- Login e logout.
- Dois perfis: `ADMIN` e `MORADOR`.
- Administrador: cadastra, edita e exclui produtos; cadastra usuários.
- Morador: consulta produtos e registra entrada/saída de estoque.
- Controle de quantidade e estoque mínimo.
- Registro de lotes e data de validade.
- Painel com alertas de estoque baixo e lotes que vencem nos próximos 7 dias.
- Histórico das últimas movimentações.
- Persistência em banco relacional.

## 6. Arquitetura
O projeto segue separação em camadas:

- `controller`: recebe requisições HTTP e seleciona as páginas.
- `service`: aplica regras de negócio.
- `repository`: acessa o banco por Spring Data JPA.
- `model`: entidades e enumerações do domínio.
- `config`: segurança e inicialização de dados.
- `templates`: páginas HTML com Thymeleaf.
- `static/css`: estilos da interface.

Fluxo:
`Navegador → Controller → Service → Repository → PostgreSQL`

## 7. Modelo de dados
- `usuarios`: credenciais e perfil de acesso.
- `produtos`: cadastro e quantidade atual.
- `lotes`: quantidade e validade por lote, ligados a um produto.
- `movimentacoes`: entrada/saída, produto, usuário e data/hora.

Relacionamentos:
- Produto 1:N Lote.
- Produto 1:N Movimentação.
- Usuário 1:N Movimentação.

## 8. Orientação a objetos
- Encapsulamento: atributos privados e métodos de acesso nas entidades.
- Abstração: serviços expõem operações de negócio sem revelar detalhes do banco.
- Associação: `Lote` referencia `Produto`; `Movimentacao` referencia `Produto` e `Usuario`.
- Enumerações: `Perfil` e `TipoMovimentacao` restringem valores possíveis.
- Responsabilidade única: controllers, services e repositories têm papéis diferentes.

A aplicação demonstra POO e arquitetura em camadas. Herança e polimorfismo não foram forçados artificialmente: podem ser aprofundados na evolução do projeto se houver uma necessidade real de domínio.

## 9. Regras de negócio principais
1. Quantidade e estoque mínimo não podem ser negativos.
2. Entrada e saída devem ter quantidade maior que zero.
3. Não é permitido retirar mais itens do que existem no estoque.
4. Lotes devem ter quantidade positiva e validade atual ou futura.
5. O alerta de estoque baixo ocorre quando quantidade atual <= estoque mínimo.
6. O alerta de validade considera lotes com vencimento entre hoje e os próximos 7 dias.
7. Somente ADMIN pode cadastrar usuários e criar/editar/excluir produtos.
8. ADMIN e MORADOR autenticados podem consultar e movimentar estoque.

## 10. Divisão sugerida para quatro integrantes
- Integrante 1: interface, requisitos, fluxo, integração e documentação.
- Integrante 2: banco de dados, entidades e repositories.
- Integrante 3: autenticação, segurança e perfis.
- Integrante 4: estoque, lotes, movimentações e alertas.

Todos devem revisar e compreender o projeto completo, pois a avaliação da ExpoTech pode abordar qualquer módulo.

## 11. Roteiro de demonstração
1. Entrar como administrador.
2. Mostrar o dashboard.
3. Cadastrar um produto.
4. Registrar um lote com validade.
5. Registrar uma entrada e uma saída.
6. Mostrar o alerta de estoque baixo e/ou validade próxima.
7. Cadastrar um morador.
8. Sair e entrar como morador.
9. Demonstrar que o morador não acessa a administração de usuários nem as ações exclusivas do administrador.

## 12. Limitações e próximos passos
Esta é uma base funcional para evolução acadêmica, não um produto pronto para produção. Antes da apresentação:
- Testar o projeto no computador de todos os integrantes.
- Criar testes automatizados para regras de estoque e permissões.
- Validar melhor formulários e mensagens de erro.
- Adicionar paginação e filtros por categoria.
- Avaliar o vínculo de lotes com movimentações para manter saldo por lote.
- Configurar variáveis de ambiente e trocar as credenciais de demonstração.
- Revisar a autorização de cada rota e operação.

## 13. Problemas comuns
- `Connection refused`: confirme `docker compose up -d` e se a porta 5432 está livre.
- `password authentication failed`: confira usuário/senha do `docker-compose.yml` e as variáveis `DB_USER`/`DB_PASSWORD`.
- Porta 8080 ocupada: encerre a outra aplicação ou altere `server.port` em `application.properties`.
- Java incompatível: confirme que o terminal está usando JDK 21.

---
DomusHub — A inteligência que organiza o seu lar.
