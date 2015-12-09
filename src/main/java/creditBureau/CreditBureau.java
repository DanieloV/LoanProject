package creditBureau;

import org.apache.axis.AxisFault;

public class CreditBureau
{
	public static int getScore(String ssn) throws Exception{
		CreditScoreServiceProxy service = new CreditScoreServiceProxy();
//		service.setEndpoint(service.getEndpoint());
		int result = service.creditScore(ssn);
		return result;
	}
}
