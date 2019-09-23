/*
 * Copyright (c) 2019 Diego Urrutia-Astorga http://durrutia.cl.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * The Web Server.
 *
 * @author Diego Urrutia-Astorga.
 * @version 0.0.1
 */
public final class Main {

    private static List<String> envios = new ArrayList<>();
    /**
     * The Logger
     */
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * The Port
     */
    private static final int PORT = 9000;

    /**
     * The Ppal.
     */
    public static void main(final String[] args) throws IOException {

        log.debug("Starting the Main ..");

        // The Server Socket
        final ServerSocket serverSocket = new ServerSocket(PORT);

        // serverSocket.setReuseAddress(true);
        log.debug("Server started in port {}, waiting for connections ..");

        // Forever serve.
        while (true) {

            // One socket by request (try with resources).
            try (final Socket socket = serverSocket.accept()) {

                // The remote connection address.
                final InetAddress address = socket.getInetAddress();

                log.debug("========================================================================================");
                log.debug("Connection from {} in port {}.");
                processConnection(socket);

            } catch (IOException e) {
                log.error("Error", e);
                throw e;
            }

        }

    }

    /**
     * Process the connection.
     *
     * @param socket to use as source of data.
     */
    private static void processConnection(final Socket socket) throws IOException {

        // Reading the inputstream
        final List<String> lines = readInputStreamByLines(socket);

        final String request = lines.get(0);
        log.debug("Request: {}");

        final PrintWriter pw = new PrintWriter(socket.getOutputStream());

        if(request.contains("POST")){
            if(!lines.isEmpty()){
                log.debug("Es POST");
                pw.println("HTTP/1.1 200 OK");
                pw.println("Server: DSM v0.0.1");
                pw.println("Date: " + new Date());
                pw.println("Content-Type: text/html; charset=UTF-8");
                // pw.println("Content-Type: text/plain; charset=UTF-8");
                pw.println();
                pw.println(webPageHTML());
                pw.flush();
            }else{
                log.debug("Campo vacio");
            }
        }else if(request.contains("GET")){
            log.debug("Es GET");
            pw.println("HTTP/1.1 200 OK");
            pw.println("Server: DSM v0.0.1");
            pw.println("Date: " + new Date());
            pw.println("Content-Type: text/html; charset=UTF-8");
            // pw.println("Content-Type: text/plain; charset=UTF-8");
            pw.println();
            pw.println(webPageHTML());
            pw.flush();
        }else{
            log.debug("Solicitud no reconocible");
        }

        log.debug("Process ended.");

    }

    /**
     * Read all the input stream.
     *
     * @param socket to use to read.
     * @return all the string readed.
     */
    private static List<String> readInputStreamByLines(final Socket socket) throws IOException {

        final InputStream is = socket.getInputStream();

        // The list of string readed from inputstream.
        final List<String> lines = new ArrayList<>();

        // The Scanner
        final Scanner s = new Scanner(is).useDelimiter("\\A");
        log.debug("Reading the Inputstream ..");
        boolean skipLine = false;

        while (true) {
            final String line = s.nextLine();
            // log.debug("Line: [{}].", line);

            if (line.length() == 0 && skipLine) {
                break;
            } else if(line.length() == 0 && !skipLine){
                skipLine = true;
            } else if(line.length() != 0 && !skipLine){
                lines.add(line);
            } else if(line.length() != 0 && skipLine){
                lines.add(line);
                envios.add(line);
            }

        }
        // String result = s.hasNext() ? s.next() : "";

        // final List<String> lines = IOUtils.readLines(is, StandardCharsets.UTF_8);
        return lines;

    }

    private static String webPageHTML(){
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<title>Chat</title>");
        sb.append("</head>");
        sb.append("<body style=\"background-color:powderblue;\">");
        sb.append("<h1 style=\"color:black;\">Chat PÃºblico</h1>");
        sb.append("<div id=\"chatVentana\">");
        sb.append("<p>\"\"</p>");
        for(int i=0 ; i<envios.size() ; i++){
            sb.append("<p>"+envios.get(i).toString()+"</p>");
        }
        sb.append("<p>\"\"</p>");
        sb.append("</div>");
        sb.append("<div id=\"chatMensaje\">");
        sb.append("    <form id = form1 method=\"post\">");
        sb.append("    <input type=\"text\" name=\"Nickname\">");
        sb.append("    <input type=\"text\" name=\"Mensaje\">");
        sb.append("    <input type=\"submit\" value=\"Enviar\">");
        sb.append("    </form>");
        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

}
