package com.bank.ekyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
@EnableScheduling
public class EkycApplication {

    public static void main(String[] args) {
        try{
        String nukk = null;
        System.out.println(nukk.toLowerCase());}
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Lỗi vượt quá chỉ số mảng");
        } catch (ArithmeticException e) {
            System.out.println("Lỗi chia cho 0");}
             catch (NullPointerException e) {
                    System.out.println("Lỗi chia cho 1");

        } catch (Exception e) {
            System.out.println("Lỗi khác");
        }

        Scanner sc = new Scanner(System.in);

        System.out.print("Nhập: ");
        String input = sc.nextLine();

        // ==========================
        // CÁCH 1: Cắt chuỗi
        // ==========================
        String[] parts = input.split("\\s+", 2);

        String ten, mssv;

        if (parts[0].matches("\\d+")) {
            mssv = parts[0];
            ten = parts[1];
        } else {
            int lastSpace = input.lastIndexOf(' ');
            ten = input.substring(0, lastSpace);
            mssv = input.substring(lastSpace + 1);
        }

        System.out.println("Tên : " + ten);
        System.out.println("MSSV: " + mssv);

        // ==========================
        // CÁCH 2: Regex
        // ==========================
        System.out.println("\n=== Cách 2: Regex ===");

        Pattern pattern = Pattern.compile("^(?:(.+?)\\s+(\\d+)|(\\d+)\\s+(.+))$");
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            String name;
            String studentId;

            if (matcher.group(1) != null) {
                name = matcher.group(1);
                studentId = matcher.group(2);
            } else {
                studentId = matcher.group(3);
                name = matcher.group(4);
            }

            System.out.println("Tên : " + name);
            System.out.println("MSSV: " + studentId);
        }

        SpringApplication.run(EkycApplication.class, args);
    }

}
