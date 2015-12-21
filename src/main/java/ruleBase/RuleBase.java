/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruleBase;

/**
 *
 * @author Daniel
 */
public class RuleBase {
    
    public static java.util.List<java.lang.Short> getBanksForCreditScore(int creditScore) {
        RuleBaseWS_Service service = new RuleBaseWS_Service();
        RuleBaseWS port = service.getRuleBaseWSPort();
        return port.getBankList(creditScore);
    }
    
}
