import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.plaf.IconUIResource;
import java.util.Scanner;

class myException extends Exception {
    String message;

    public myException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "我的错误：" + message;
    }
}

public class test1 {
    private static Logger logger = LogManager.getLogger(test1.class);

    public static void main(String[] args) throws myException{
        Scanner scan = new Scanner(System.in);

        System.out.println("请输入课程代号：(1-3)：");
        try {
            if (scan.hasNextInt()) {
                int a = scan.nextInt();
                if(a>10){
                    logger.error("数字大于10");
                    throw new myException("数字大于10");
                }
                if (a < 1 || a > 3) {
                    logger.error("数字请在1到3内");
                    throw new NumberFormatException("数字请在1到3内");
                }
                System.out.println("cc");
            } else {
                logger.error("请输入数字");
                throw new Exception("请输入数字");
            }
        }
        catch (myException e){
            System.out.println("异常0");
            logger.warn("异常0");
            e.printStackTrace();
        }
        catch (NumberFormatException e){
            System.out.println("异常1");
            logger.warn("异常1");
            e.printStackTrace();
        } catch (Exception e) {
            //System.out.println(e.getMessage());
            System.out.println("异常2");
            logger.warn("异常2");
            e.printStackTrace();
            // System.exit(1);
            return;
        } finally {
            logger.info("欢迎提出建议！");
            //System.out.println("欢迎提出建议！");
        }

    }
}
