package bg.sofia.uni.fmi.mjt.bookmarks.server;

import bg.sofia.uni.fmi.mjt.bookmarks.contracts.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.contracts.ResponseStatus;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.Logger;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.DatabaseContext;
import bg.sofia.uni.fmi.mjt.bookmarks.server.sessions.Session;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.IdGenerator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server implements Runnable {
    private final String host;
    private final int port;
    private final Logger logger;
    private final DatabaseContext context;
    private final ByteBuffer messageBuffer;
    private final CommandExecutor commandExecutor;
    private boolean isStarted = true;

    public Server(ServerOptions options) {
        this.port = options.port();
        this.host = options.host();
        this.logger = options.logger();
        this.context = options.context();
        this.messageBuffer = ByteBuffer.allocate(options.bufferSize());
        this.commandExecutor = CommandExecutor.configure(options.sessionStore(), options.context(), options.logger());
    }

    @Override
    public void run() {
        logger.logInfo("Server started.");
        context.load();
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (isStarted && !Thread.interrupted()) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();


                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    try {
                        if (key.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) key.channel();

                            messageBuffer.clear();
                            int r = socketChannel.read(messageBuffer);
                            if (r <= 0) {
                                logger.logInfo(
                                    "Nothing to read, closing channel for client " + socketChannel.getRemoteAddress());
                                disconnect(key);
                                continue;
                            }

                            handleKeyIsReadable(key, messageBuffer);
                        } else if (key.isAcceptable()) {
                            handleKeyIsAcceptable(selector, key);
                        }
                    } catch (IOException e) {
                        disconnect(key);
                        String traceId = IdGenerator.generateId();
                        logger.logError(
                            "There is a problem with the server socket: " + e.getMessage() + ". Trace id: " + traceId);
                        logger.logException(e, traceId);
                    }
                    keyIterator.remove();
                }
            }

            for (SelectionKey selectionKey : selector.keys()) {
                try {
                    disconnect(selectionKey);
                } catch (Exception e) {
                    //ignored
                }
            }

            selector.close();

        } catch (IOException e) {
            String traceId = IdGenerator.generateId();
            logger.logError(
                "There is a problem with the server socket: " + e.getMessage() + ". Trace id: " + traceId);
            logger.logException(e, traceId);
        }

        context.shutdown();
        logger.logInfo("Server stopped");
    }

    public void stopServer() {
        isStarted = false;
    }

    private void handleKeyIsReadable(SelectionKey key, ByteBuffer buffer) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        buffer.flip();
        String message = new String(buffer.array(), 0, buffer.limit()).trim();
        logger.logInfo("Message received from client " + socketChannel.getRemoteAddress() + " : " + message);
        var commands = message.split(System.lineSeparator());
        for (var cmd : commands) {
            Response response;
            try {
                response = commandExecutor.execute(cmd, new Session(socketChannel, null));
            } catch (Exception e) {
                String traceId = IdGenerator.generateId();
                logger.logError(
                    "There is a problem with the server socket: " + e.getMessage() + ". Trace id: " + traceId);
                logger.logException(e, traceId);
                response = new Response("Internal server error. Trace id: " + traceId, ResponseStatus.ERROR);
            }

            var splitResponse = response.getDataMessage().split(System.lineSeparator());
            for (var responseMsg : splitResponse) {
                buffer.clear();
                buffer.put((responseMsg + System.lineSeparator()).getBytes());
                buffer.flip();
                socketChannel.write(buffer);
                logger.logInfo(
                    "Response sent to client " + socketChannel.getRemoteAddress() + " : " +
                        response.getDataMessage().trim());
            }
        }
    }

    private void handleKeyIsAcceptable(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
        logger.logInfo("Connection accepted from client " + accept.getRemoteAddress());
    }

    private void disconnect(SelectionKey key) throws IOException {
        key.channel().close();
        key.cancel();
    }
}
