package com.bottele;

/*
 * Importando as bibliotecas relacionadas ao Telegram
 */
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/*
 * Importando as bibliotecas relacionadas ao Java
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;


/*
 * Criação da classe Bot que estende de TelegramLongPollingBot (Cria um bot de longa espera)
 */
public class Bot extends TelegramLongPollingBot {
    /*
     * Definindo variáveis privadas responsáveis por armazenar caminhos dos arquivos
     */
    private static final String CAMINHO_ARQUIVO_FAQ = "faq.csv";
    private static final String PREFIXO_ARQUIVO_AGENDA = "agenda_";
    private static final String EXTENSAO_ARQUIVO_AGENDA = ".csv";
    private Map<String, String> mapaFAQ;

    /*
     * Construtor da classe Bot
     * Chama o método carregarFAQ() para carregar as perguntas e respostas do FAQ e atribuí-las ao mapa mapaFAQ
     */
    public Bot() {
        mapaFAQ = carregarFAQ();
    }

    /*
     * Esse método é uma sobrescrita do método da classe TelegramLongPollingBot 
     * Verifica se há alguma mensagem recebida no objeto Update e verifica se a mensagem possui um texto
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                String mensagemTexto = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();
                /*
                 * Se o texto for /start -> Chama o método enviarMensagemInicial()
                 */
                if (mensagemTexto.equals("/start")) {
                    enviarMensagemInicial(chatId);
                /*
                 * Se o texto for /comandos -> Chama o método enviarListaComandos()
                 */
                } else if (mensagemTexto.equals("/comandos")) {
                    enviarListaComandos(chatId);
                /*
                 * Caso contrário -> Chama o método responderPergunta()
                 */
                } else {
                    String pergunta = normalizarTexto(mensagemTexto).toLowerCase();
                    responderPergunta(chatId, pergunta);
                }
            }
        }
    }

    /*
     * Método responsável por enviar uma mensagem de boas-vindas ao chat do usuário 
     */
    private void enviarMensagemInicial(long chatId) {
        String mensagem = "🤖 Olá! Bem-vindo ao nosso bot.\n\nDigite /comandos para ver a lista de comandos disponíveis.";
        /*
         * Instancia SendMessage, recebendo o id do chat convertido para string e a mensagem de boas-vindas
         */
        SendMessage response = new SendMessage(String.valueOf(chatId), mensagem);
        /*
         * Cria um bloco try para lidar com possíveis exceções na chamada do método execute(response)
         */
        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /*
     * Método responsável por enviar a lista de comandos disponíveis ao chat do usuário 
     */
    private void enviarListaComandos(long chatId) {
        String mensagem = "Comandos disponíveis:\n\n" +
                "▶️/start - Inicia o bot\n\n" +
                "⚙️/comandos - Exibe a lista de comandos\n\n" +
                "🤔/perguntas - Lista de perguntas disponíveis\n\n" +
                "🗓️/agenda - Consultar a agenda";
        SendMessage response = new SendMessage(String.valueOf(chatId), mensagem);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /*
     * Método responsável por responder uma pergunta feita pelo usuário 
     * O chat é identificado pelo chatId e a pergunta é identificada pelo texto da pergunta
     */
    private void responderPergunta(long chatId, String pergunta) {
        /*
         * Se o text for igual /perguntas, é criado um objeto StringBuilder para construir a mensagem de resposta
         */
        if (pergunta.equals("/perguntas")) {
            StringBuilder sb = new StringBuilder();
            sb.append("Perguntas disponíveis:\n\n");
            /*
             * Percorre as chaves do mapa que contem as perguntas disponiveis
             */
            for (String perguntaDisponivel : mapaFAQ.keySet()) {
                sb.append("⤿ ").append(perguntaDisponivel).append("\n");
            }
            /*
             * Envia a mensagem com o metodo enviarMensagem()
             */
            enviarMensagem(chatId, sb.toString());
        } else if (pergunta.equals("/agenda")) {
            enviarMensagem(chatId, "Por favor, digite o ID do usuário para consultar a agenda\n(Ex: /agenda_1)");
        /*
         * Verfica se a pergunta começa com /agenda_
         */
        } else if (pergunta.startsWith("/agenda_")) {
            /*
             * Processa a pergunta para extrair o ID do usuário
             */
            String[] partes = pergunta.split("_");
            if (partes.length == 2) {
                String userId = partes[1];
                String caminhoArquivoAgenda = PREFIXO_ARQUIVO_AGENDA + userId + EXTENSAO_ARQUIVO_AGENDA;
                if (arquivoExiste(caminhoArquivoAgenda)) {
                    String conteudoAgenda = lerConteudoArquivo(caminhoArquivoAgenda);
                    if (conteudoAgenda.isEmpty()) {
                        enviarMensagem(chatId, "A agenda para o usuário " + userId + " está vazia.");
                    } else {
                        StringBuilder agendaBuilder = new StringBuilder();
                        agendaBuilder.append("✍️ Agenda do usuário \n\n");
                        String[] linhas = conteudoAgenda.split("\n");
                        /*
                         * Loop para percorer as linhas da agenda
                         */
                        for (int i = 1; i < linhas.length; i++) {
                            /*
                             * Aqui cada coluna da agenda e dividida com a virgula
                             */
                            String[] colunas = linhas[i].split(",");
                            /*
                             * Verifica se a agenda possui as 5 colunas para enviar todas as informações da agenda
                             */
                            if (colunas.length >= 5) {
                                /*
                                 * Atribui as variaveis correspondentes as colunas e utiliza o metodo tirm() para remover espaçoes em branco
                                 */
                                String dataHora = colunas[0].trim();
                                String descricao = colunas[1].trim();
                                String tipo = colunas[2].trim();
                                String recursos = colunas[3].trim();
                                String participantes = colunas[4].trim();

                                /*
                                 * Cada informação e adicionada ao agendaBuilder para construir uma string finmal da agenda
                                 */
                                agendaBuilder.append("📋 Tarefa ").append(i).append(":\n");
                                agendaBuilder.append("    ⤿ Descrição: ").append(descricao).append("\n");
                                agendaBuilder.append("    ⤿ Data/Hora: ").append(dataHora).append("\n");
                                agendaBuilder.append("    ⤿ Tipo: ").append(tipo).append("\n");
                                agendaBuilder.append("    ⤿ Recursos: ").append(recursos).append("\n");
                                agendaBuilder.append("    ⤿ Participantes: ").append(participantes).append("\n\n");
                            }
                        }
                        enviarMensagem(chatId, agendaBuilder.toString());
                    }
                } else {
                    enviarMensagem(chatId, "A agenda para o usuário " + userId + " não foi encontrada.");
                }
            } else {
                enviarMensagem(chatId, "ID de usuário inválido.");
            }
        } else {
            String resposta = mapaFAQ.get(pergunta);
            if (resposta != null) {
                enviarMensagem(chatId, resposta);
            } else {
                enviarMensagem(chatId, "Desculpe, não encontrei uma resposta para essa pergunta.");
            }
        }
    }

    /*
     * Método responsável por enviar uma mensagem ao chat do usuário 
     */
    private void enviarMensagem(long chatId, String mensagem) {
        SendMessage response = new SendMessage(String.valueOf(chatId), mensagem);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /*
     * Esse metodo utiliza BufferedReader para ler o conteúdo do arquivo CSV especificado pelo caminho CAMINHO_ARQUIVO_FAQ.
     */
    private Map<String, String> carregarFAQ() {
        Map<String, String> faq = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(CAMINHO_ARQUIVO_FAQ))) {
            String linha;
            /*
             *  Divide a linha em duas partes usando o ; como delimitador e verifica se existem exatamente duas partes
             */
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length == 2) {
                    String pergunta = normalizarTexto(partes[0].trim().toLowerCase());
                    String resposta = partes[1].trim();
                    faq.put(pergunta, resposta);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return faq;
    }
    

    /*
     * Método responsável por normalizar um texto removendo acentos e caracteres especiais
     * Retorna o texto normalizado
     */
    private String normalizarTexto(String texto) {
        String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        textoNormalizado = textoNormalizado.replaceAll("[^\\p{ASCII}]", "");
        return textoNormalizado;
    }

    /*
     * Método responsável por verificar se um arquivo existe em um determinado caminho
     * Retorna true se o arquivo existir e false caso contrário
     */
    private boolean arquivoExiste(String caminhoArquivo) {
        return Files.exists(Paths.get(caminhoArquivo));
    }

    /*
     * Método responsável por ler o conteúdo de um arquivo
     * Retorna o conteúdo do arquivo como uma string
     */
    private String lerConteudoArquivo(String caminhoArquivo) {
        StringBuilder conteudo = new StringBuilder();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                conteudo.append(linha).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conteudo.toString();
    }

    /*
     * Método responsável por retornar o nome do bot
     */
    @Override
    public String getBotUsername() {
        return "";
    }

    /*
     * Método responsável por retornar o tokendo bot
     */
    @Override
    public String getBotToken() {
        return "";
    }
}
