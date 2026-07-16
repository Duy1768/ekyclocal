package com.bank.ekyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;
import java.sql.SQLException;

@SpringBootApplication
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

        SpringApplication.run(EkycApplication.class, args);
    }

}
