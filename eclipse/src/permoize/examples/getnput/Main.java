package permoize.examples.getnput;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import permoize.CommonMemoizer;
import permoize.CommonMemoizeContainer;
import permoize.Memoizer;
import permoize.StreamMemoizeEntryList;

public class Main {
	public static void main(String[] args) {
		try {
			Memoizer memoizer = new CommonMemoizer(new CommonMemoizeContainer(new StreamMemoizeEntryList("memoi.zer")));
			
			String name = memoizer.recollect("Hello", () -> {
				System.out.println("Hi there!! Welcome to your first visit to this application!!!");
				System.out.println("Please enter your name, such that I can remember you in the future:");
				
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				
				return br.readLine();
			});
			
			System.out.println("Hello " + name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
