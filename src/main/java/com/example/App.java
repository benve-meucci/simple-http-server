package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class App {
    public static void main(String[] args) {
        try {
            System.out.println("Server in avvio!");
            ServerSocket server = new ServerSocket(8080);
            while (true) {
                Socket s = server.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                String line;
                line = in.readLine();
                String filePath = line.split(" ")[1];
                do {
                    line = in.readLine();
                    System.out.println(line);
                } while (!line.isEmpty());

                File file = new File("htdocs" + filePath);
                if (file.exists()) {
                    sendBinaryFile(s, file);
                } else {
                    String msg = "File non trovato";
                    out.writeBytes("HTTP/1.1 404 Not found\n");
                    out.writeBytes("Content-Length: " + msg.length() + "\n");
                    out.writeBytes("Content-Type: text/plain\n");
                    out.writeBytes("\n");
                    out.writeBytes(msg);
                }
                s.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("errore durante l'istanza del server");
            System.exit(1);
        }
    }

    private static void sendBinaryFile(Socket socket, File file) throws IOException {
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        output.writeBytes("HTTP/1.1 200 OK\n");
        output.writeBytes("Content-Length: " + file.length() + "\n");
        output.writeBytes("Content-Type: "+getContentType(file)+"\n");
        output.writeBytes("\n");
        InputStream input = new FileInputStream(file);
        byte[] buf = new byte[8192];
        int n;
        while ((n = input.read(buf)) != -1) {
            output.write(buf, 0, n);
        }
        input.close();
    }

    private static String getContentType(File f){
        String [] s = f.getName().split("\\.");
        String ext = s[s.length-1];
        switch(ext) {
            default:
            case "html":
            case "htm":
                return "text/html";
            case "png":
                return "image/png";
            case "css":
                return "text/css";
        }

    }
}