# Find User Service

Aplicação REST para consulta de usuários utilizando FTS (Full Text Search) do MongoDB.

[![Build Status](https://travis-ci.com/gustavo-flor/find-user-service.svg?branch=main)](https://travis-ci.com/gustavo-flor/find-user-service)

---

## Instalação

### Dependências

- Instalação Docker, [link para o guia completo no site oficial](https://docs.docker.com/docker-hub/) 🐳.

> Executar `docker -v` para verificar a instalação.

### Passo a passo

- Clone o repositório (`$ git clone git@github.com:gustavo-flor/find-user-service.git`);
  
- Acesse a pasta clonada (`$ cd find-user-service`) 🗃️;
  
- Execute o build da aplicação (`$ ./mvnw clean install -DskipTests=true`);

- Suba o serviço e suas dependências (`$ docker compose up`);

- Estamos prontos para utilizar a aplicação através do endpoint `http://localhost:8080/search` 🎉.

## Documentação API

Acesse o [ambiente de "produção"](https://find-user-service.herokuapp.com/search?query=pedra) para testar a aplicação.

> É importante notar que não é um host dedicado, portanto lentidões ou limitações de requests são esperadas. No primeiro acesso poderá ocorrer uma delay para resposta.

### User Controller

GET | `/search?query={terms}` | `json`

Obtém os usuários que foram encontrados com base nos termos enviados, ordenados conforme a relevância dos usuários e a compatibilidade com os termos.

---

**terms**: Cada palavra separada por um espaço é considera um termo (caso exista a necessidade de usar buscar por uma frase especifico basta enviar a frase entre aspas duplas)

Exemplos:

- `query=Pedro Gabriel`: A aplicação irá buscar por usuários que contemplem o termo "Pedro" ou "Gabriel" (ou seus derivados, explico melhor isso no tópico de "Detalhes + Objetivos...");

- `query="Pedro Gabriel"`: A aplicação irá buscar por usuários que contemplem exatamente o termo "Pedro Gabriel";

- `query="Pedro" Gabriel`: A aplicação irá buscar por usuários que contemplem exatamente o termo "Pedro" ou contemplem "Gabriel" (ou seus derivados).

---

| Parâmetro | Descrição | Obrigatório |
|-----------|-----------| ----------- |
| query | Termo procurado. | Sim |
| from | Página, contagem de páginas começa em 0. Valor padrão: 0. | Não |
| size | Limite de usuários por página. Valor padrão: 15. | Não |
| debug | Adiciona no retorno dos usuários a relevância e a nota de compatibilidade com os termos enviados. Valor padrão: false, Valores permitidos \[true, false]. | Não |

Exemplo:

```shell
curl http://localhost:8080/search?query=%22pedra%22
```

```json5
{
  "from": 0,
  "size": 15,
  "data": [
    {
      "id": "9f64853b-f164-4b13-a3d7-1fdb9977e516",
      "name": "Jeferson Pedra",
      "username": "jeferson.pedra"
    },
    {
      "id": "b4026e35-932c-4943-b0db-7dd31eca5597",
      "name": "Stefane Pedra Zimiani",
      "username": "stefane.pedra.zimiani"
    }
  ]
}
```

> Retorno do usuário com `debug: true`

```json5
{
  "textScore": 1.5, 
  "id": "482c46f3-a27c-4f1a-abd0-3039ede21fdd",
  "name": "Gustav Berghahn",
  "username": "gustav.berghahn",
  "relevance": 3  
}
```

## Objetivos + Detalhes da implementação

A necessidade era construir uma aplicação escalável e de alta performance, que fosse capaz de buscar através de um ou mais termos os usuários de uma determinada base de dados e ordena-los conforme sua relevância. Sabendo disso foi escolhido criar uma aplicação Spring com MongoDB para utilizar a feature do FTS.

> Não temos um endpoint para inclusão de novos usuários, eles são incluídos ao subir a aplicação com base nos arquivos presentes na pasta `resources` do projeto java.

Quais foram as motivações para o uso do Spring?

Primeira e mais importante é a familiaridade com o framework, mas também a facilidade em criar e configurar uma API REST com ele, além de facilitar a configuração com o mongodb e demais plataformas de deploy (Heroku).

Quais foram as motivações para o uso do MongoDB?

Conhecendo nossa necessidade, podemos perceber que não teremos que lidar com nenhum relacionamento dentro do nosso banco de dados, então isso já seria um ótimo motivo, mas o principal motivo não posso mentir :P foi sua feature de busca de texto... o FTS tadããã!!!

Vantagens e desvantagens do Full Text Search...

Através de indexação dos textos para a busca o FTS acaba trazendo muita performance em comparação com opções como o "LIKE", essa solução é especialista em buscas por termos, portanto muitas vezes consegue executar a busca de forma muito mais acertiva e rápida.

Sua maior desvantagem e inimiga é: FTS não é um "LIKE", e no início é díficil entender isso, não podemos enviar metade de um termo e acreditar que a buscar vai funcionar conforme esperamos. 

Sabendo disso para uma busca ainda mais acertiva é ideal conhecer de fato todas as possibilidades que o FTS nos proporciona através de sua [documentação](https://docs.mongodb.com/manual/reference/operator/query/text/).

## Até breve

Fique a vontade para criar melhorias para essa solução ⚗, grande abraço!
