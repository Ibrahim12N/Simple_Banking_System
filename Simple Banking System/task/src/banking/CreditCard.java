package banking;

import java.util.Random;

public class CreditCard {
    String cardNumber;
    String PIN;
    int balance;

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPIN() {
        return PIN;
    }

    public CreditCard() {
        this.cardNumber = cardCreator();
        this.PIN=PINCreator();
        this.balance=0;
    }

    String cardCreator() {
        StringBuilder cardNum=new StringBuilder();
        cardNum.append("400000");
        Random random=new Random();
        for(int i=0;i<9;i++)
            cardNum.append(random.nextInt(10));
        int sum=0;
        for (int i=0; i<cardNum.length();i++){
            int x=Character.getNumericValue(cardNum.charAt(i));
            int order=i+1;
            if(order%2!=0){
                x*=2;
                if(x>9)
                    x-=9;
            }

            sum+=x;
        }
        int checkSum=0;
        if(sum%10!=0)
            checkSum=10-sum%10;
        cardNum.append(checkSum);
        return  cardNum.toString();
    }
    String PINCreator() {
        Random random=new Random();
        StringBuilder PIN=new StringBuilder();
        for(int i=0;i<4;i++){
            int x=random.nextInt(10);
            System.out.println(x);
            if (i==0 &&x==0){
                i--;
            } else
            PIN.append(x);}
        return  PIN.toString();
    }

}
