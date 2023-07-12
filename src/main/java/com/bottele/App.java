package com.bottele;

/*
 * Importando bibliotecas API Telegram
 */
import org.telegram.telegrambots.meta.TelegramBotsApi;  // Biblioteca para interagir com a API do Telegram
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;  // Exceção relacionada à API do Telegram
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;  // Classe para receber as atualizações do bot

public final class App {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);  // Cria uma instância do TelegramBotsApi usando DefaultBotSession
            
            botsApi.registerBot(new Bot());  // Registra o bot usando a instância criada e a classe Bot
            
            System.out.println("Bot criado com sucesso! ");  // Exibe uma mensagem indicando que o bot foi criado com sucesso
        } catch (TelegramApiException e) {
            e.printStackTrace();  // Exibe o rastreamento da pilha de exceções em caso de erro
        }
    }
}
