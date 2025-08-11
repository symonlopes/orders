# Orders Project
Um projeto de API em REST para salvar, consultar e recuperar pedidos.

# Duplicidade

Para evitar duplicidade de pedidos, foi criado um cache usando código do vendedor, código do produto e código 
do cliente que está comprando.

Se um pedido com esses mesmos elementos for recebido mais de 1 vez numa janela de tempo 
configurável, o pedido é rejeitado.

# Stack Escolhida

## Banco de Dados
O banco de dados escolhido foi MongoDB.
Considerando o volume mensal de pedidos de 200 mil pedidos, o banco foi carregado com 16 milhões de registros, resultando
em um tamanho total de 3.5GB no total.

Dois índices foram criados, no campo "status" e "createdAt".

## SpringBoot com Virtual Threads

# Spec

Máquina do MongoDB (KVM):
4 GB de RAM
4 CPUs 5000MHz

Máquina do Java 21 (KVM):
8 GB de RAM
4 CPUs 5000MHz


# Resultados

A base foi populada com 5 milhões de registro (aproximadamente 1 mês de histórico de pedidos) para os testes seguintes. 

O REDIS ficou com cerca de 400MB de RAM para uma vazão de 1000 requests por segundo.
O JAVA, com um consumo médio de 5GB de RAM para uma vazão de 1000 requests por segundo.

Cenário 1: Sem Virtual Threads (Platform Threads)
Métrica	Valor	Observação
RDELAY	22.57s	O tempo total que o processo passou esperando na fila do agendador do kernel. Um valor alto indica contenção de CPU.
THR	233	O número de threads de plataforma (Platform Threads) ativas. Cada uma representa um recurso significativo de memória.
CPU%	175%	O percentual de uso de CPU.


Cenário 2: Com Virtual Threads
Métrica	Valor	Observação
RDELAY	6.68s	O tempo total de atraso do processo. Este valor é significativamente menor que o do cenário anterior.
THR	42	O número de threads de plataforma (Platform Threads) ativas. Uma redução drástica em comparação com o cenário anterior.
CPU%	151%	O percentual de uso de CPU.