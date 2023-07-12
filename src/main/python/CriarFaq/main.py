import csv

faq = [
    ["O que é Programação Orientada a Objetos?", "A Programação Orientada a Objetos é um paradigma de programação que utiliza objetos para representar entidades do mundo real e suas interações."],
    ["Quais são os pilares da Orientação a Objetos?", "Os pilares da Orientação a Objetos são encapsulamento, herança e polimorfismo."],
    ["Para que serve Herança?", "A herança permite que uma classe herde características e comportamentos de outra classe, facilitando a reutilização de código e a criação de hierarquias de classes."],
    ["Por que utilizar encapsulamento?", "O encapsulamento permite ocultar os detalhes internos de implementação de uma classe, fornecendo uma interface controlada para interagir com o objeto, melhorando a segurança e a manutenibilidade do código."],
    ["Quais são as diferenças entre uma classe e um objeto?", "Uma classe é uma estrutura que define as características e comportamentos de um tipo de objeto. Por outro lado, um objeto é uma instância específica de uma classe, que possui seus próprios valores para as características definidas na classe. Em resumo, uma classe é uma definição e um objeto é uma realização concreta dessa definição."]
]

# Abre o arquivo faq.csv em modo de escrita
with open("faq.csv", "w", newline="", encoding="utf-8") as file:
    writer = csv.writer(file, delimiter=";")
    # Escreve as perguntas e respostas no arquivo
    writer.writerows(faq)
