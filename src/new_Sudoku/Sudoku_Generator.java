package new_Sudoku;

import java.util.Random;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Sudoku_Generator extends JFrame {
	
	public String Gen_url; //the sudoku string that is the input 
	int[][] a = new int[9][9];
	int[][] b = new int[10][10];
	int k1,k2;
	
	///Constructor///
	
	public Sudoku_Generator(int md) {
		
		generate(a);
		
		random_gen(1,a);
		random_gen(0,a);
		
		random_change(k1,k2,a);
		
		Gen_url = GenString(k1,k2,a,b, md);
	}
	
	//Fills a 9x9 Sudoku table with valid values
	public static void generate(int[][] a) {
		int k=1,n=1;
		for(int i=0;i<9;i++) {
			k=n;
			for(int j=0;j<9;j++) {
				if(k<=9) {
					a[i][j]=k;
					k++;
				}
				else {
					k=1;
					a[i][j]=k;
					k++;
				}
			}
			n=k+3;
			if(k==10) {
				n=4;
			}
			if(n>9) {
				n=(n%9)+1;
			}
		}
	}
	
	//Generate random numbers to interchange rows & columns within a group
	public static void random_gen(int check,int[][] a) {
		int k1,k2,max=2,min=0;
		Random r= new Random();
		
		//There are three groups.So we are using for loop three times
		for(int i=0;i<3;i++) {
			k1=r.nextInt(max-min+1)+min;
			
			//This while is just to ensure k1 is not equal to k2.
			do {
				k2=r.nextInt(max-min+1)+min;
			} while(k1==k2);
			max+=3;
			min+=3;
			
			//calling a function to interchange the selected rows
			if(check==1) {
				permutation_row(k1,k2,a);
			}
			//calling a function to interchange the selected columns
			else if(check==0) {
				permutation_col(k1,k2,a);
			}
		}
	}
	
	//Interchange 2 rows
	public static void permutation_row(int k1,int k2,int[][] a) {
		int temp;
		//k1 and k2 are two rows that we are selecting to interchange.
		for(int j=0;j<9;j++) {
			temp=a[k1][j];
			a[k1][j]=a[k2][j];
			a[k2][j]=temp;
		}
	}
	
	//Interchange 2 columns
	public static void permutation_col(int k1,int k2,int[][] a) {
		int temp;
		//k1 and k2 are two columns that we are selecting to interchange.
		for(int j=0;j<9;j++) {
			temp=a[j][k1];
			a[j][k1]=a[j][k2];
			a[j][k2]=temp;
		}
	}
	
	//Generate random numbers to interchange groups of rows & columns
	public static void random_change(int k1,int k2,int[][] a) {
		int n[]={0,3,6};
		int counter=1;
		
		Random rand = new Random();
		
		for(int i=0;i<2;i++) {
			k1=n[rand.nextInt(n.length)];  
			do {
				k2=n[rand.nextInt(n.length)];
			} while(k1==k2);
			
			if(counter==1) {
				row_change(k1,k2, a);
			}
			else {
				col_change(k1,k2, a);
			}
			counter++;
		}
	}
	
	//Interchanging two groups of rows
	public static void row_change(int k1,int k2,int[][] a) {
		int temp;
		for(int n=1;n<=3;n++) {
			for(int i=0;i<9;i++) {
				temp=a[k1][i];
				a[k1][i]=a[k2][i];
				a[k2][i]=temp;
			}
			k1++;
			k2++;
		}
	}

	//Interchanging two groups of columns
	public static void col_change(int k1,int k2,int[][] a) {
		int temp;
		for(int n=1;n<=3;n++) {
			for(int i=0;i<9;i++) {
				temp=a[i][k1];
				a[i][k1]=a[i][k2];
				a[i][k2]=temp;
			}
			k1++;
			k2++;
		}
	}

	//Generate the string being used
	public static String GenString(int k1,int k2,int[][] a, int[][] b, int md) {
		int i,j;
		
		Random rand = new Random();
		
		//Striking out
		for(k1=0;k1<9;k1++) {
			for(k2=0;k2<9;k2++) {
				strike_out(k1,k2, a);
			}
		}
		
		//Adjust Difficulty of the given puzzle
		int rep=0;
		if (md==2) {
			rep = 5;
		}
		else if (md==3) {
			rep = 10;
		}
		if (md!=1) {
			int x1,x2;
			int cnt1=0;
			int u1[]={0,1,2,3,4,5,6,7,8};
			do {
				x1=u1[rand.nextInt(u1.length)];
				x2=u1[rand.nextInt(u1.length)];
				if (a[x1][x2]!=0) {
					a[x1][x2]=0;
					cnt1++;
				}
			} while (cnt1<rep);
		}
		
		//table b[10][10]
		for(i=0;i<9;i++) {
			for(j=0;j<9;j++) {
				b[i][j] = a[i][j];
			}
		}
		
		//table to string
		int p=9;
		StringBuilder stringBuilder = new StringBuilder();
		for(i=0;i<9;i++) {
			for(j=0;j<9;j++) {
				if (b[i][j] == 0) {
					stringBuilder.append('.');
				}
				else {
					stringBuilder.append(b[i][j]);
				}
			}
			stringBuilder.insert(p, 'a');
			p = p +10;
		}
		String generated = stringBuilder.toString();
		return generated;
	}
	
	//Striking out cells
	public static void strike_out(int k1, int k2, int[][] a) {
		int row_from;
		int row_to;
		int col_from;
		int col_to;
		int i,j,b,c;
		int rem1,rem2;
		int flag;
		int count=9;
		
		for(i=1;i<=9;i++) {
			flag=1;
			for(j=0;j<9;j++) {
				if(j!=k2) {
					if(i!=a[k1][j]) {
						continue;
					}
					else {
						flag=0;
						break;
					}
				}
			}
			if(flag==1) {
				for(c=0;c<9;c++) {
					if(c!=k1) {
						if(i!=a[c][k2]) {
							continue;
						}
						else {
							flag=0;
							break;
						}
					}
				}
			}
			if(flag==1) {
				rem1=k1%3; 
				rem2=k2%3;
				row_from=k1-rem1;
				row_to=k1+(2-rem1);
				col_from=k2-rem2;
				col_to=k2+(2-rem2);
				for(c=row_from;c<=row_to;c++) {
					for(b=col_from;b<=col_to;b++) {
						if(c!=k1 && b!=k2) {
							if(i!=a[c][b]) {
								continue;
							}
							else {
								flag=0;
								break;
							}
						}
					}
				}
			}
			if(flag==0) {
				count--;
			}
		}
		if(count==1) {
			a[k1][k2]=0;
			count--;
		}
	}
}