# Sistema de Gestão Hospitalar

## Objetivo

O objetivo do projeto é criar uma API para organizar um sistema público de saúde.

## Contextualização

O Sistema Único de Saúde (SUS) é um dos maiores e mais complexos sistemas de saúde pública do mundo, abrangendo desde o simples atendimento para avaliação da pressão arterial, por meio da Atenção Básica, até o transplante de órgãos, garantindo acesso integral, universal e gratuito para toda a população do país. Com a sua criação, o SUS proporcionou o acesso universal ao sistema público de saúde, sem discriminação. A atenção integral à saúde, e não somente aos cuidados assistenciais, passou a ser um direito de todos os brasileiros, desde a gestação e por toda a vida, com foco na saúde com qualidade de vida, visando a prevenção e a promoção da saúde. O objetivo desse projeto é criar uma ferramenta para auxiliar o SUS, evitar desperdício e potencializar os recursos a partir dos pacientes.

Ao final do programa, além de apresentar a API criada, cada squad deverá expor quais são os pontos de melhoria e quais seriam os próximos passos no projeto, caso fossem seguir adiante.

## Requisitos técnicos obrigatórios

Para o projeto é necessário que as squads se utilizem dos aprendizados repassados pelo programa AceleraDev, portanto a solução deve ser construída de acordo com os seguintes requisitos:

- Banco de dados;
- Desenvolvimento do backend e APIs com Java Spring Boot;
- Testes unitários são obrigatórios

## Definições do Sistema

- Todo hospital tem um estoque contendo diversos produtos.
- Um estoque tem produtos e suas respectivas quantidades.
- Dentro do estoque também tem banco de sangue
- O hospital também possui leitos.
- Ao encontrar um paciente é importante o recomendar para um hospital mais próximo e que tenha vaga disponível.
- Caso o hospital precise de um produto, por exemplo, um banco de sangue, é importante fazer o envio do hospital mais próximo ao local.
- Caso o hospital tenha apenas 4 itens ou 4 litros ele terá apenas o suficiente para o próprio hospital.

## Exemplos de chamadas:

A partir da API que gerencia os hospitais dentro do SUS o usuário conseguirá, por exemplo:

- Cadastrar o hospital
- A partir da localização do paciente indicar o hospital mais próximo.
- Realizar check-in/ check-out do paciente no hospital
- Verificar quantos leitos disponíveis no hospital
- Cadastrar produtos e suas respectivas quantidades
- Cadastrar e gerenciar banco de sangue

### /v1/hospitais/{id}

Método: GET
Retorna as informações do hospital, por exemplo:

- Nome
- Endereço
- Número de leitos
- Números de leitos disponíveis

### /v1/hospitais/{id}/estoque

Método: GET
Retorna as informações dos produtos existentes estoque, por exemplo.

### /v1/hospitais/{id}/estoque/{produto}

Método: GET
Retorna mais detalhes de um produto.

- Nome
- Descrição
- Quantidade

### /v1/hospitais/{id}/pacientes

Método: GET
Retorna o nome dos pacientes dentro do hospital.

### /v1/hospitais/{id}/pacientes/{paciente}

Método: GET
Retorna todas as informações do paciente cadastrado, por exemplo:

- Nome completo
- CPF
- Data de nascimento
- Sexo
- Data de entrada no hospital.
