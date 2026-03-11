package io.github.poeticrainbow.rptools;

import io.github.poeticrainbow.rptools.commands.RegisteredCommands;

import java.util.Arrays;

public class RPTools {
    public static void main(String[] args) {

        if (args.length < 1 || !RegisteredCommands.REGISTRY.containsKey(args[0])) {
            printHelp();
            return;
        }
        try {
            RegisteredCommands.REGISTRY.get(args[0]).execute(Arrays.copyOfRange(args, 1, args.length));
            System.out.println("\nThank you for using RPTools!");
        } catch (Exception e) {
            System.out.println("An error occured.");
            e.printStackTrace();
            printHelp();
        }
    }

    public static void printHelp() {
        System.out.println("Available commands: ");
        RegisteredCommands.REGISTRY.forEach((s, command) -> {
            System.out.println(s);
            System.out.println("    " + command.usage());
        });
    }

    static {
        RegisteredCommands.register();
    }
}
