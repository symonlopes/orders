# orders
Test project for admission process.

# Assumptions

Orders must have an archivation policy. For example, order CLOSED with more than 10 days from creation time, must be archived.
A duplicated order is an order by same seller, to same customer, with same items, in a time window of N minutes.


# Questionamentos

Para o Produto Externo B, existe requisito de tempo de resposta para consultar os pedidos ?
Seria necessário entender melhor o cenário de duplicação. Isso determina qual solução seria melhor.


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


Virtual Threads - Java 21

# Sem virtual threads
2 milhões de registros
Tempo total: 3:34

PID              SYSCPU             USRCPU             RDELAY              VGROW             RGROW             RUID                 EUID                 ST             EXC             THR             S             CPUNR              CPU             CMD        1/1
6756             4.95s              12.14s             22.57s                 0B            256.0K             zezo                 zezo                 --               -             233             S                 2             175%             java
6734             0.74s              0.73s              0.54s                 0B                0B             redis                redis                --               -               5             S                 3              15%             redis-server

# Com virtual threads
2 milhões de registros
Tempo total: 3:34

PID              SYSCPU             USRCPU             RDELAY              VGROW             RGROW             RUID                 EUID                 ST             EXC             THR             S             CPUNR              CPU             CMD        1/1
7133             3.84s             11.00s              6.68s                 0B            128.0K             zezo                 zezo                 --               -              42             S                 2             151%             java
7116             0.52s              0.62s              0.59s                 0B             -0.2M             redis                redis                --               -               5             S                 0              12%             redis-server


Cenário 1: Sem Virtual Threads (Platform Threads)
Métrica	Valor	Observação
PID (java)	6756	O processo da aplicação Java.
RDELAY	22.57s	O tempo total que o processo passou esperando na fila do agendador do kernel. Um valor alto indica contenção de CPU.
THR	233	O número de threads de plataforma (Platform Threads) ativas. Cada uma representa um recurso significativo de memória.
CPU%	175%	O percentual de uso de CPU.


Cenário 2: Com Virtual Threads
Métrica	Valor	Observação
PID (java)	7133	O novo processo da aplicação Java.
RDELAY	6.68s	O tempo total de atraso do processo. Este valor é significativamente menor que o do cenário anterior.
THR	42	O número de threads de plataforma (Platform Threads) ativas. Uma redução drástica em comparação com o cenário anterior.
CPU%	151%	O percentual de uso de CPU.