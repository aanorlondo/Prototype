package model;

public class Poisson 
{	

	public static int getPoisson(double lambda) 
	{
		double L = Math.exp(-lambda);
		double p = 1.0;
		int k = 0;

		while (p > L)
		{
			k++;
			p *= Math.random();
		}
		return k - 1;
	}
}