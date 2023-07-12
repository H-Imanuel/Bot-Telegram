import csv


# Classe para representar um usuário
class Usuario:
    def __init__(self, nome, id):
        self.nome = nome
        self.id = id
        self.tarefas = []


# Classe para representar uma tarefa
class Tarefa:
    def __init__(self, data_hora, descricao, tipo, recursos, participantes):
        self.data_hora = data_hora
        self.descricao = descricao
        self.tipo = tipo
        self.recursos = recursos
        self.participantes = participantes


# Função para cadastrar um novo usuário
def cadastrar_usuario():
    nome = input("Digite seu nome: ")
    id = input("Digite seu ID: ")
    usuario = Usuario(nome, id)
    return usuario


# Função para acessar um usuário existente
def acessar_usuario(usuarios):
    id = input("Digite seu ID: ")
    for usuario in usuarios:
        if usuario.id == id:
            return usuario
    print("Usuário não encontrado.")
    return None


# Função para criar uma nova tarefa
def criar_tarefa(usuario):
    data_hora = input("Digite a data e hora da tarefa (ex: 06/07/2023 10:00): ")
    descricao = input("Digite a descrição da tarefa: ")
    tipo = input("Digite o tipo da tarefa: ")
    recursos = input("Digite os recursos necessários para a tarefa (separados por espaço) : ")
    participantes = input("Digite os participantes da tarefa (separados por espaço): ").split(',')
    tarefa = Tarefa(data_hora, descricao, tipo, recursos, participantes)
    usuario.tarefas.append(tarefa)


# Função para editar uma tarefa existente
def editar_tarefa(usuario):
    listar_tarefas(usuario)
    indice = int(input("Digite o índice da tarefa que deseja editar: "))
    if indice >= 0 and indice < len(usuario.tarefas):
        tarefa = usuario.tarefas[indice]
        data_hora = input(f"Digite a nova data e hora da tarefa [{tarefa.data_hora}]: ") or tarefa.data_hora
        descricao = input(f"Digite a nova descrição da tarefa [{tarefa.descricao}]: ") or tarefa.descricao
        tipo = input(f"Digite o novo tipo da tarefa [{tarefa.tipo}]: ") or tarefa.tipo
        recursos = input(f"Digite os novos recursos necessários para a tarefa (separados por espaço)  [{tarefa.recursos}]: ") or tarefa.recursos
        participantes = input(f"Digite os novos participantes da tarefa (separados por espaço) [{', '.join(tarefa.participantes)}]: ").split(',')
        tarefa.data_hora = data_hora
        tarefa.descricao = descricao
        tarefa.tipo = tipo
        tarefa.recursos = recursos
        tarefa.participantes = participantes
    else:
        print("Índice inválido.")


# Função para excluir uma tarefa existente
def excluir_tarefa(usuario):
    listar_tarefas(usuario)
    indice = int(input("Digite o índice da tarefa que deseja excluir: "))
    if indice >= 0 and indice < len(usuario.tarefas):
        del usuario.tarefas[indice]
    else:
        print("Índice inválido.")


# Função para listar as tarefas do usuário
def listar_tarefas(usuario):
    if usuario.tarefas:
        print("Tarefas:")
        for i, tarefa in enumerate(usuario.tarefas):
            print(f"{i}: {tarefa.descricao}")
    else:
        print("Nenhuma tarefa encontrada.")


# Função para salvar as tarefas do usuário em um arquivo CSV
def salvar_tarefas(usuario):
    nome_arquivo = f"agenda_{usuario.id}.csv"
    with open(nome_arquivo, "w", newline="", encoding='utf-8') as arquivo:
        writer = csv.writer(arquivo, quoting=csv.QUOTE_NONE, escapechar='\\')
        writer.writerow(["Data/Hora", "Descrição", "Tipo", "Recursos", "Participantes"])
        for tarefa in usuario.tarefas:
            writer.writerow([tarefa.data_hora, tarefa.descricao, tarefa.tipo, tarefa.recursos, ', '.join(tarefa.participantes)])
    print(f"Tarefas salvas no arquivo {nome_arquivo}.")


# Função principal
def main():
    usuarios = []
    while True:
        print("\n--> MENU DE USUÁRIO <--")
        print("[1] - Cadastrar Usuário")
        print("[2] - Acessar Usuário")
        print("[0] - Sair")
        opcao = input("Digite sua opção: ")

        if opcao == "1":
            usuario = cadastrar_usuario()
            usuarios.append(usuario)
            print("Usuário cadastrado com sucesso.")
        elif opcao == "2":
            usuario = acessar_usuario(usuarios)
            if usuario:
                while True:
                    print("\n--> MENU DE TAREFAS <--")
                    print("[1] - Criar Tarefa")
                    print("[2] - Editar Tarefa")
                    print("[3] - Excluir Tarefa")
                    print("[4] - Listar Tarefas")
                    print("[5] - Salvar Tarefas como Arquivo")
                    print("[0] - Sair")
                    opcao_tarefa = input("Digite sua opção: ")

                    if opcao_tarefa == "1":
                        criar_tarefa(usuario)
                        print("Tarefa criada com sucesso.")
                    elif opcao_tarefa == "2":
                        editar_tarefa(usuario)
                    elif opcao_tarefa == "3":
                        excluir_tarefa(usuario)
                    elif opcao_tarefa == "4":
                        listar_tarefas(usuario)
                    elif opcao_tarefa == "5":
                        salvar_tarefas(usuario)
                    elif opcao_tarefa == "0":
                        break
                    else:
                        print("Opção inválida.")
        elif opcao == "0":
            break
        else:
            print("Opção inválida.")


if __name__ == "__main__":
    main()