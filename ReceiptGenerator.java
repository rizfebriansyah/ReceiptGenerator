// STUDENT NAME: MUHAMMAD RIZQI FEBRIANSYAH
// STUDENT NUMBER: 190428446

import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

public class ReceiptGenerator {

    private static final String RETURNS_POLICY_URL = "http://www.FixAllPhones.co.uk/returns";
    private static final String TERMS_AND_CONDITIONS_URL = "http://www.FixAllPhones.co.uk/terms";
    private static final String SERVICE_REVIEW_URL = "https//uk.trustpilot.com/review/www.FixAllPhones.co.uk";
    private static final String REFERRAL_URL = "http://www.FixAllPhones.co.uk/phonefriend";
    private static final String SOCIAL_MEDIA = "FaceBook and Twitter";
    private static final String COMPANY_SLOGAN = "FixAllPhones - repairs you can trust";
    private static final int VAT_NUMBER = 123456789;
    //static final variables are constants, they cannot be changed by methods once assigned.
    //By convention they are written in all caps,
    //with words separated by the underscore for readability.

    private static final String[] LOCATIONS = {
            "King's Cross",
            "Victoria",
            "Waterloo",
            "Euston",
            "Liverpool Street",
            "London Bridge",
            "Paddington",
            "Marylebone"
    };

    private static final String[] PAYMENT_TYPES = {
            "cash",
            "credit card",
            "debit card",
            "bitcoin"
    };
    private static final String[] Preposition = {
            "a",
            "an",
            "the",
            "with",
            "of",
            "and",};
    private static final String[] TEMPLATE = {
            "%n****************************************%n* %s *%n****************************************%n%n", // + COMPANY_SLOGAN
            "Receipt - %s%n%n", // + receipt reference no
            "FixAll - %s%n%n", // + shop location
            "VAT No: %d%n%n", // + vat no
            "Served by: %s%n%n", // + staff member's first name (TODO and initial of surname?)
            "---------------------------------------------%nYour order:%n%n",
            "%s%n%n", // + costs breakdown
            "Subtotal\t\t\t\t£%.2f%n", // + subtotal (TODO: make it a task to make the receipts capable of being in euros? [ie make the £/€ in the receipt configurable] necessary because the Kings Cross branch is seeing lots of European business because of being next to St Pancras International)
            "Tax (VAT @ 20%%)\t\t\t\t£%.2f%n%n", // + tax (TODO: should tax rate also be configurable? VAT may change post Brexit)
            "TOTAL : %d item%s\t\t\t\t£%.2f%n%n", // + number of items, s or no s (single or plural), total amount payable
            "PAID (%s) :\t\t\t£%.2f%n%n", // + payment type (credit card/debit card/cash/bitcoin/other crypto), amount paid
            "TO PAY :\t\t\t\t£%.2f%n%n", // + amount left to pay
            "---------------------------------------------%n%n%n", //no additional info
            "Exchange and returns policy - %s%n%n", // + returns policy url
            "All repairs guaranteed for 18 months%nRepair T&Cs - %s%n%n%n", // + terms & conditions url
            "Rate our Service%n%s%n%n%n", // + 3rd party review site url
            "Get 20%% off your order when you refer a friend - see our web page for details%n%s%n%n", // + referral url
            "Follow us on %s%n" // + our social media channels
    };

    private static String dateAndTime;
    private static String shopLocation;
    private static String staffName;
    private static String itemDescription;
    private static String itemisedCosts;
    private static int numberOfItemsPurchased;
    private static double subtotal;
    private static double vat;
    private static double total;
    private static double outstandingBalance;
    private static double paid;
    private static boolean keepGoing;
    private static String paymentMethod;
    private static Scanner in;
    private static double unitPrice;
    private static int numberOfUnits;

    public static void create() {
        in = new Scanner(System.in);

        dateAndTime = getDateAndTime();
        shopLocation = getShopLocationFromUser();
        staffName = getFromUser("your first name");
        staffName = capitalise(staffName);

        itemisedCosts = "";
        numberOfItemsPurchased = 0;
        subtotal = 0;

        keepGoing = true;

        while (keepGoing) {

            getItemDescriptionAndUnitPrice();
            calculateTotalCost();
            askUserIsThereAnyotherItemOrService();

        }

        vat = getVat(subtotal);

        total = addVat(subtotal);

        paid = checkAmountAndRoundOff(paid);
        paymentMethod = toCheckWhetherThePaymentMethodIsValid(paymentMethod);

        in.close();

        calculateOutstandingBalance();

        //print the formatted receipt
        printReceipt();
    }

    private static String getDateAndTime() {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
        return String.format("%1$te/%<Tb/%<tY::%<tH%<tM%<tz(%<tZ)", now);
    }

    private static String getShopLocationFromUser() {
        System.out.println("SHOP LOCATIONS:");
        for (int i = 0; i < LOCATIONS.length; i++) {
            System.out.println((i + 1) + ") " + LOCATIONS[i]);
        }
        int choice = getIntFromUser("The number that corresponds to your shop");
        return LOCATIONS[choice - 1];
    }

    private static void getItemDescriptionAndUnitPrice() {

        itemDescription = getFromUser("Item or service user is being charged for");
        itemDescription = capitaliseEveryWordIgnoringPreposition(itemDescription);

        unitPrice = getDoubleFromUser("the unit price (without VAT) of " + itemDescription);
        numberOfUnits = getIntFromUser("the number of " + itemDescription);

    }

    private static void calculateTotalCost() {

        itemisedCosts += addItemisedCostToReceipt(itemDescription, unitPrice, numberOfUnits);
        subtotal += (unitPrice * numberOfUnits);
        numberOfItemsPurchased += numberOfUnits;
    }

    private static boolean askUserIsThereAnyotherItemOrService() {
        boolean checkYesOrNo = true;
        while (checkYesOrNo) {
            String more = getFromUser("another item or service? Yes/No");
            if (more.trim().toLowerCase().startsWith("n")) {

                checkYesOrNo = false;
                keepGoing=false;
            }
            else{
                checkYesOrNo=false;
                keepGoing=true;}
        }




        return checkYesOrNo;

    }

    private static String capitalise(String s) {
        if (s == null) {
            return ("");
        }
        s = s.trim();
        if (s.length() < 2) {
            return s.toUpperCase();
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private static String capitaliseEveryWordIgnoringPreposition(String word1) {

        word1 = word1.trim();
        word1=word1.replaceAll("\\s+"," ");
        String b[] = word1.split(" ");
        String sep = "";

        for (String word : b) {
            if (isPreposition(word)) {
                sep += " "+ (word.toLowerCase());
            }
            else if (word.length() < 2) {
                sep += " "+(word.toUpperCase());}
            else  {
                sep += " " + word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();

            }
        }
        return sep.trim();
    }

    private static boolean isPreposition(String word) {
        boolean isPreposition = false;
        for (String prep : Preposition) {
            if (ignorePreposition(prep, word)) {
                isPreposition = true;
                break;
            }
        }
        return isPreposition;
    }

    private static boolean ignorePreposition(String prep, String word) {
        return prep.equalsIgnoreCase(word);
    }

    private static String addItemisedCostToReceipt(String itemDescription, double unitPrice, int numberOfUnits) {
        //format: numberOfUnits x itemDescription @ £unitPrice each: £(unitPrice*NumberOfUnits)
        return String.format("%d x %s @ £%.2f each\t\t£%.2f%n", numberOfUnits, itemDescription, unitPrice, unitPrice * numberOfUnits);
    }

    private static void calculateOutstandingBalance() {
        outstandingBalance = total - paid;
    }

    private static String getFromUser(String prompt) {
        System.out.print("Enter " + prompt + ": ");
        return in.nextLine();
    }

    private static int getIntFromUser(String prompt) {
        boolean keepGoing = true;
        int i = 0;
        while (keepGoing) {
            System.out.print("Enter " + prompt + ": ");
            i = in.nextInt();

            in.nextLine();

            if (i < 1) { //make sure the number is more than 0
                System.out.println("Invalid number. Number must be greater than zero.");
            } else {
                keepGoing = false;
            }
        }
        return i;
    }

    private static double getDoubleFromUser(String prompt) {
        boolean keepGoing = true;
        double i = 0;

        while (keepGoing) {
            System.out.print(("Enter " + prompt + " : "));
            i = in.nextDouble();

            in.nextLine(); //read the rest of the line (probably just a newline character)

            if (i <= 0) {
                System.out.println("Invalid number. Number must be greater than zero.");
            } else {
                keepGoing = false;
            }
        }

        return i;
    }

    private static int checkAmountAndRoundOff(double amount) {
        amount = getDoubleFromUser("the deposit amount (must be at least 38 and no more than 192.0)");
        boolean keepGoing = true;

        double roundOff = 0;
        while (keepGoing) {

            if (amount < total * 0.2) {
                System.out.println("Deposit is too small");
                amount = getDoubleFromUser("the deposit amount (must be at least 38 and no more than 192.0)");
            } else if (amount > total) {
                System.out.println("Deposit is too large");
                amount = getDoubleFromUser("the deposit amount (must be at least 38 and no more than 192.0)");
            } else {
                keepGoing = false;
                roundOff = Math.round(amount);

            }
        }

        return (int) roundOff;
    }

    private static String toCheckWhetherThePaymentMethodIsValid(String compare) {


        int compareTo = 0;
        boolean keepGoing = true;
        while (keepGoing) {
            compare = getFromUser("deposit payment method");
            compare=compare.trim();
            for (int i = 0; i < PAYMENT_TYPES.length; i++) {
                if (isPaymentType(compare)) {
                    compare = compare.trim().toLowerCase();

                    keepGoing = false;
                    break;
                } else if (!isPaymentType(compare) && i == 1) {

                    System.out.println("Payment type not recognised");
                    compareTo++;

                } else if (compareTo == 1) {
                    System.out.println("Please enter one of " +printPaymentMethods());
                    compareTo++;

                }
            }
        }
        return compare;
    }
    private static String printPaymentMethods(){
        return  Arrays.toString(PAYMENT_TYPES).replace("[", " ").replace("]", " ").trim();
    }


    private static boolean isPaymentType(String type) {
        boolean isPaymentType = false;
        for (String payment : PAYMENT_TYPES) {
            if (checkPaymentType(payment, type)) {
                isPaymentType = true;
                break;
            }
        }
        return isPaymentType;
    }

    private static boolean checkPaymentType(String payment, String type) {
        return payment.equalsIgnoreCase(type);
    }


    private static void printReceipt() {
        String s = "";

        for (int i = 0; i < TEMPLATE.length; i++) {
            s += String.format(TEMPLATE[i], getTemplateData(i));
        }

        /*
         TODO need to fix the tab spacing in the itemised costs
         and the other bits between the dashes (the costs breakdown etc)
         */
        System.out.println(s);
    }

    private static Object[] getTemplateData(int line) {
        if (line == 0) {
            return new Object[]{COMPANY_SLOGAN};
        }
        if (line == 1) {
            return new Object[]{dateAndTime};
        }
        if (line == 2) {
            return new Object[]{shopLocation};
        }
        if (line == 3) {
            return new Object[]{VAT_NUMBER};
        }
        if (line == 4) {
            return new Object[]{staffName};
        }
        if (line == 6) {
            return new Object[]{itemisedCosts};
        }
        if (line == 7) {
            return new Object[]{subtotal};
        }
        if (line == 8) {
            return new Object[]{vat};
        }
        if (line == 9) {
            return new Object[]{numberOfItemsPurchased, numberOfItemsPurchased > 1 ? "s" : "", total};
        }
        if (line == 10) {
            return new Object[]{paymentMethod, paid};
        }
        if (line == 11) {
            return new Object[]{outstandingBalance};
        }
        if (line == 13) {
            return new Object[]{RETURNS_POLICY_URL};
        }
        if (line == 14) {
            return new Object[]{TERMS_AND_CONDITIONS_URL};
        }
        if (line == 15) {
            return new Object[]{SERVICE_REVIEW_URL};
        }
        if (line == 16) {
            return new Object[]{REFERRAL_URL};
        }
        if (line == 17) {
            return new Object[]{SOCIAL_MEDIA};
        } else {
            return new Object[]{""}; //any lines that don't need extra data
        }
    }

    private static double addVat(double amount) {
        double vat = getVat(amount);
        return amount + vat;
    }

    private static double getVat(double amount) {
        return (amount * 20.0) / 100.0; //vat = 20%
    }

    public static void main(String[] args) {
        ReceiptGenerator.create();
    }
}