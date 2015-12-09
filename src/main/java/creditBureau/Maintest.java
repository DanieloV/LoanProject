/* (C) Minuba 2015. All rights reserved */
package creditBureau;

public class Maintest
{

	public static void main(String[] args) throws Exception
	{
		// TODO Auto-generated method stub
		String ssn = "213214-1234";
		int res = CreditBureau.getScore(ssn);
		System.out.println(res);
		
	}

}
