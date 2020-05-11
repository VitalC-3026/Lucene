import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;

public class FilePre {
    //private Logger logger = LogManager.getLogger(this.getClass());

    public static boolean ValidEmail(String address){
        try{
            int index_at = address.indexOf("@");
            if(index_at != -1 && index_at != 1){
                int index_first_dot = address.indexOf(".");
                if(index_first_dot != -1 && index_first_dot != 1){
                    int index_last_dot = address.lastIndexOf(".");
                    String sub = address.substring(index_last_dot+1);
                    if(!sub.equals("com")){
                        return false;
                    }
                    if(index_first_dot+1 < index_at){
                        String substring = address.substring(index_first_dot+1,index_at);
                        int index_sub_dot = substring.lastIndexOf(".");
                        if(index_first_dot + 1 == index_sub_dot){
                            return false;
                        }
                        if(index_sub_dot+1==index_at){
                            return false;
                        }
                        while(substring.contains(".") && index_first_dot + 1 < index_sub_dot){
                            index_sub_dot = substring.lastIndexOf(".");
                            if(index_sub_dot > index_first_dot + 1){
                                substring = substring.substring(index_first_dot + 1, index_sub_dot);
                            }
                            else{
                                break;
                            }

                        }
                        if(index_first_dot + 1 == index_sub_dot ){
                            return false;
                        }
                    }
                    else if(index_first_dot > index_at+1){
                        int index_sub_dot = index_first_dot + 1;
                        if(index_sub_dot+1<index_last_dot){
                            String substring = address.substring(index_first_dot+1,index_last_dot);
                            while(substring.contains(".") && index_sub_dot + 1 < index_last_dot){
                                index_sub_dot = substring.indexOf(".");
                                if(index_sub_dot + 1 < index_last_dot){
                                    substring = substring.substring(index_sub_dot + 1, index_last_dot);
                                }
                                else{
                                    break;
                                }
                            }
                            if(index_sub_dot + 1==index_last_dot){
                                return false;
                            }
                        }
                        else if(index_sub_dot + 1==index_last_dot){
                            return false;
                        }

                    }
                    else{
                        return false;
                    }
                }
                else if(index_first_dot == 1){
                    return false;
                }
            }
            else{
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            return true;
        }
        return true;
    }
    public String[] getSender(@NotNull File file) throws Exception {
        String[] sender_info = new String[2];
        if(file.isFile() && file.exists()){
            String FileName = file.getPath();
            FileReader fr = new FileReader(FileName);
            BufferedReader br = new BufferedReader(fr);
            String content, sender_email, sender;
            while((content = br.readLine()) != null)
            {
                if(content.contains("From:") && content.contains("@")) {
                    int index_from = content.indexOf(":");
                    sender_email = content.substring(index_from + 1);
                    sender_info[0] = sender_email;
                    if(!ValidEmail(sender_email)){
                        System.out.println("The e-mail address is not valid!");
                        String error_info = file.getPath() + " " + sender_email;
                        //logger.error("The e-mail address is not valid! " + error_info);
                    }
                    else{
                        sender_email = sender_email.trim();
                        while (sender_email.startsWith("　")) {//这里判断是不是全角空格
                            sender_email = sender_email.substring(1, sender_email.length()).trim();
                        }
                        while (sender_email.endsWith("　")) {
                            sender_email = sender_email.substring(0, sender_email.length() - 1).trim();
                        }
                    }
                }
                if(content.contains("X-From:")){
                    int index_from = content.indexOf(":");
                    sender = content.substring(index_from + 1);
                    sender = sender.trim();
                    while (sender.startsWith("　")) {//这里判断是不是全角空格
                        sender = sender.substring(1, sender.length()).trim();
                    }
                    while (sender.endsWith("　")) {
                        sender = sender.substring(0, sender.length() - 1).trim();
                    }
                    sender_info[1] = sender;
                }
            }
            br.close();
            fr.close();
            //判断数组是否有内容;
            return sender_info;
        }
        else{
            sender_info[0] = "";
            sender_info[1] = "";
            return sender_info;
        }
    }
    public LinkedList<String[]> getReceiver(@NotNull File file) throws Exception{
        LinkedList<String[]> receiver_info = new LinkedList<>();
        if(file.exists() && file.isFile()){
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String content;
            String[] receiver_email,receiver;
            while((content = br.readLine()) != null){
                if(content.contains("To:") && content.contains("@")){
                    int index_to = content.indexOf(":");
                    content = content.substring(index_to + 1);
                    receiver_email = content.split(",");
                    for (int i = 0; i < receiver_email.length; i++) {
                        if (!ValidEmail(receiver_email[i])) {
                            String error_info = file.getPath() + " " + receiver_email[i];
                            //logger.error("The e-mail address is not valid!" + error_info);
                            for(int j = i + 1; j < receiver_email.length; j++)
                            {
                                receiver_email[j - 1] = receiver_email[j];
                            }
                        }
                    }
                    receiver_info.add(receiver_email);
                    //System.out.println(file.getPath() + Arrays.toString(receiver_email));
                }
                if(content.contains("X-To:")){
                    int index_to = content.indexOf(":");
                    content = content.substring(index_to + 1);
                    receiver = content.split(",");
                    for(int i = 0; i < receiver.length; i++){
                        receiver[i] = receiver[i].trim();
                        while (receiver[i].startsWith("　")) {//这里判断是不是全角空格
                            receiver[i] = receiver[i].substring(1, receiver[i].length()).trim();
                        }
                        while (receiver[i].endsWith("　")) {
                            receiver[i] = receiver[i].substring(0, receiver[i].length() - 1).trim();
                        }
                    }
                    receiver_info.add(receiver);
                    //System.out.println(file.getPath() + Arrays.toString(receiver));
                }
            }
            br.close();
            fr.close();
            //boolean l = (receiver_info == null);
            //System.out.println(file.getPath() + "receiver_info ?= null" + l);
            return receiver_info;
        }
        else {
            String[] none = new String[0];
            receiver_info.add(none);
            //boolean l = (receiver_info == null);
            //System.out.println(file.getPath() + "receiver_info ?= null" + l);
            return receiver_info;
        }
    }
    public String getSubject(@NotNull File file) throws Exception{
        String content, subject = "";
        if(file.exists() && file.isFile()){
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            while((content = br.readLine()) != null){
                if(content.contains("Subject:"))
                {
                    subject = content;
                    break;
                }
            }
            br.close();
            fr.close();
        }
        return subject;
    }
    public String getContent(@NotNull File file) throws Exception{
        String content, textContent = "";
        boolean flag = false;
        if(file.exists() && file.isFile()){
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            //只实现提取第一段内容
            while((content = br.readLine()) != null){
                if(content.equals("") && !flag){
                    flag = true;
                }
                else if(content.equals("") && flag){
                    flag = false;
                    break;
                }
                if (flag) {
                    textContent += content;
                }
            }
        }
        return textContent;
    }
}
