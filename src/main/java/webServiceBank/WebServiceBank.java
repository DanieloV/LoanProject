/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webServiceBank;

/**
 *
 * @author Daniel
 */
public class WebServiceBank {

    public static double getOffer(long ssn, long loanAmount, long loanDuration, int creditScore) {
        NewWebService_Service service = new NewWebService_Service();
        NewWebService port = service.getNewWebServicePort();
        return port.getOffer(ssn, loanAmount, loanDuration, creditScore);
    }

}
