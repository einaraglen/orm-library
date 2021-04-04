package no.ntnu.stud.einagl;

public class Main {

    public static void main(String[] args) {
        //oppgave 1
        //Connector.inserBook(1111, 4444, "TEST", "TEST");

        //oppgave 2 address and number since database does not have username and password
        Connector.updateClientAddressOf("Ola Norman", "TEST 123");
        Connector.updateClientNumberOf("Ola Norman", 99999999);
        System.out.println("");

        //oppgave 3
        //Connector.deleteLoanOf(4012);

        //oppgave 4
        Connector.readBorrowersOf("C++ Primer");

        //oppgave 5
        Connector.readBookBasedOnDate("2020/11/20");

        //oppgave 6
        Connector.readLoansPerBranch();
    }
}
