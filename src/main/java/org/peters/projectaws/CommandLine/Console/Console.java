package org.peters.projectaws.CommandLine.Console;

import java.util.Scanner;

public class Console {
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void runConsole() {
        System.out.println("AWS Resource Management Console");
        System.out.println("Type 'help' to see available commands");
        
        boolean running = true;
        
        while (running) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();
            String[] parts = input.split("\\s+");
            
            if (parts.length == 0) continue;
            
            String command = parts[0].toLowerCase();
            
            switch (command) {
                case "help":
                    showHelp();
                    break;
                    
                case "ec2":
                    if (parts.length < 2) {
                        System.out.println("Usage: ec2 <create|list|start|stop|terminate> [options]");
                        break;
                    }
                    handleEC2Command(parts);
                    break;
                    
                case "exit":
                case "quit":
                    running = false;
                    System.out.println("Exiting AWS Resource Management Console...");
                    break;
                    
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
            }
        }
        
        scanner.close();
    }
    
    private static void showHelp() {
        System.out.println("Available commands:");
        System.out.println("  ec2 create <instance-type> <ami-id> [key-name] [security-group]");
        System.out.println("  ec2 list");
        System.out.println("  ec2 start <instance-id>");
        System.out.println("  ec2 stop <instance-id>");
        System.out.println("  ec2 terminate <instance-id>");
        System.out.println("  help - Show this help message");
        System.out.println("  exit | quit - Exit the console");
    }
    
    private static void handleEC2Command(String[] parts) {
        String subCommand = parts[1].toLowerCase();
        
        switch (subCommand) {
            case "create":
                if (parts.length < 4) {
                    System.out.println("Usage: ec2 create <instance-type> <ami-id> [key-name] [security-group]");
                    return;
                }
                String instanceType = parts[2];
                String amiId = parts[3];
                String keyName = parts.length > 4 ? parts[4] : null;
                String securityGroup = parts.length > 5 ? parts[5] : null;
                createEC2Instance(instanceType, amiId, keyName, securityGroup);
                break;
                
            case "list":
                listEC2Instances();
                break;
                
            case "start":
                if (parts.length < 3) {
                    System.out.println("Usage: ec2 start <instance-id>");
                    return;
                }
                startEC2Instance(parts[2]);
                break;
                
            case "stop":
                if (parts.length < 3) {
                    System.out.println("Usage: ec2 stop <instance-id>");
                    return;
                }
                stopEC2Instance(parts[2]);
                break;
                
            case "terminate":
                if (parts.length < 3) {
                    System.out.println("Usage: ec2 terminate <instance-id>");
                    return;
                }
                terminateEC2Instance(parts[2]);
                break;
                
            default:
                System.out.println("Unknown EC2 command. Type 'help' for available commands.");
        }
    }
    
    private static void createEC2Instance(String instanceType, String amiId, String keyName, String securityGroup) {
        System.out.println("Creating EC2 instance...");
        System.out.println("Instance Type: " + instanceType);
        System.out.println("AMI ID: " + amiId);
        if (keyName != null) System.out.println("Key Name: " + keyName);
        if (securityGroup != null) System.out.println("Security Group: " + securityGroup);
        
        // TODO: Implement actual EC2 instance creation using AWS SDK
        // This is where you would integrate with the AWS SDK to create the instance
        
        System.out.println("EC2 instance creation initiated successfully!");
    }
    
    private static void listEC2Instances() {
        System.out.println("Listing EC2 instances...");
        // TODO: Implement actual EC2 instance listing using AWS SDK
        System.out.println("No instances found or not implemented yet.");
    }
    
    private static void startEC2Instance(String instanceId) {
        System.out.println("Starting EC2 instance: " + instanceId);
        // TODO: Implement actual EC2 instance start using AWS SDK
        System.out.println("Instance start requested for: " + instanceId);
    }
    
    private static void stopEC2Instance(String instanceId) {
        System.out.println("Stopping EC2 instance: " + instanceId);
        // TODO: Implement actual EC2 instance stop using AWS SDK
        System.out.println("Instance stop requested for: " + instanceId);
    }
    
    private static void terminateEC2Instance(String instanceId) {
        System.out.println("Terminating EC2 instance: " + instanceId);
        // TODO: Implement actual EC2 instance termination using AWS SDK
        System.out.println("Instance termination requested for: " + instanceId);
    }
}
