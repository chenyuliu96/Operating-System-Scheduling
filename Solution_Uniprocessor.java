import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Solution_Uniprocessor {

	public static void main(String[] args) throws FileNotFoundException {
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		int []random = makeRandom("random-number.txt");
		String input="";
		boolean flag = false;
		if(args.length==2) {input = args[1]; flag = true;}
		else {input = args[0];}
		int[][] schedule = readInput(input);// original schedule
		printArr(schedule,"original");
		sortArr(schedule);//sorted schedule
		printArr(schedule,"sorted");
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


		Machine_UNI M = new Machine_UNI();// make a new Machine_UNI
		// add every Process_UNI to the Machine_UNI's arrayList
		for(int i=0;i<schedule.length;i++) {
			Process_UNI p = new Process_UNI(i, schedule[i][0],schedule[i][1],schedule[i][2],schedule[i][3]);
			p.CPUTimeRemain = p.CPUtime;
			M.PArr.add(p);
		}

		M.processDone = M.PArr.size();
		if(flag) {
		System.out.println("\nThis detailed printout gives the State_UNI and remaining burst for each Process_UNI\n");
		}
		while(M.processDone>0) {

		//	System.out.println("prograss is "+ M.processDone);
			if(true) {
				if(flag) {
				System.out.printf("Before cycle%5d:   ", M.cycleCount);}
				int forhowlong =0;
				for(Process_UNI p:M.PArr) {
					switch(p.State_UNI) {
						case running: forhowlong = p.runFor;
						break;
						case blocked:forhowlong = p.blockedFor; p.IOtime++;

						break;
						case unstarted: forhowlong = 0;
						break;
						case terminated: forhowlong = 0;
						break;
						case ready: forhowlong = 0; p.waitingTime++;

					}
					if(flag) {
					System.out.printf("%10s  %2d ",p.State_UNI,forhowlong);}
					//M.finishTime = p.finishTime;
				}
			if(flag) {
			System.out.println();}}

			doBlocked(M, random);
			doRunning(M, random);
			doArrive(M);
			doReady(M,random);
			M.cycleCount++;

		}
		System.out.println("\nThe scheduling algorithm used was Uniprocessor\n");

		for(Process_UNI p:M.PArr) {

			p.printProcess();
			M.IO_Uti += p.IOtime;
			M.CPU_Uti +=p.CPUtime;
			M.finishTime = p.finishTime;
			M.turnaroundTotal+=p.finishTime-p.Arrival;
			M.waitTotal+=p.waitingTime;
		}

		M.printSummary();
	}
	public static void doBlocked(Machine_UNI M,int[] random) {
		if(M.BlockedQ.isEmpty()) {
			return;
		}
		Process_UNI p = M.BlockedQ.get(0);
		p.blockedFor --;

		//p.CPUTimeRemain++; import!!!!
		p.State_UNI = State_UNI.blocked;
//		System.out.println("now is blocking"+ p.blockedFor);
		if(p.blockedFor <=0) {
//			System.out.println("switch to run");
			M.BlockedQ.remove(p);
			M.RunningQ.add(p);
			p.State_UNI = State_UNI.running;
			p.runFor = randomOS(p.cpuBurst, random,M)+1;

		}
	//	p.blockedFor --;
	}

	public static void doRunning(Machine_UNI M, int[] random) {
		if(M.RunningQ.isEmpty()) {
			return;
		}
		Process_UNI p = M.RunningQ.get(0);
		p.State_UNI = State_UNI.running;
		p.runFor--;
		p.CPUTimeRemain --;

		if(p.CPUTimeRemain <=0) {
			p.State_UNI = State_UNI.terminated;
			p.finishTime = M.cycleCount;
			M.RunningQ.remove(p);
			M.processDone --;
			return;
		}

		if(p.runFor ==0) {
			M.RunningQ.remove(p);
			M.BlockedQ.add(p);
			p.State_UNI = State_UNI.blocked;
			p.CPUTimeRemain++;
			p.blockedFor = randomOS(p.IOBurst, random,M);
		}
	}
	public static void doArrive(Machine_UNI M) {
		if(M.arrivedProcess == M.PArr.size()) {
			return;
		}
		for(int i=0;i<M.PArr.size();i++) {
			Process_UNI p = M.PArr.get(i);
			if(p.Arrival == M.cycleCount) {
				M.ReadyQ.add(p);
				p.State_UNI = State_UNI.ready;
				M.arrivedProcess++;
			}

		}
	}
	public static void doReady(Machine_UNI M, int[] random) {
		if(M.ReadyQ.isEmpty()) {
			return;
		}
		Process_UNI p = M.ReadyQ.get(0);
		if(M.RunningQ.isEmpty() && M.BlockedQ.isEmpty()) {
			M.RunningQ.add(p);
			p.State_UNI= State_UNI.running;
			M.ReadyQ.remove(p);
			p.runFor = randomOS(p.cpuBurst, random,M);
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 *
	 * helper function
	 */
	public static void printArr(int[][] arr, String status) {
		if(status.equals("sorted")) {
		System.out.print("\nThe (sorted) input is:   "+ arr.length+ " ");}
		else {
			System.out.print("The original input was:  "+ arr.length+ " ");
		}
		for(int[]f: arr) {
			for(int x:f) {System.out.print(x+" ");}
			System.out.print("  ");
			}
		if(status.equals("sorted")) {
			System.out.println();
		}
		}


	public static void sortArr(int[][] schedule) {
		final Comparator<int[]> arrayComparator = new Comparator<int[]>() {
			@Override
			public int compare(int[] o1, int[] o2) {
				return o1[0]-o2[0];}
		};
		Arrays.sort(schedule, arrayComparator);
	}
	public static int[][] readInput(String filename) throws FileNotFoundException {
		FileReader file = new FileReader(filename);
		Scanner sc = new Scanner(file);
		int caseNum = sc.nextInt();
		int[][] arr = new int[caseNum][4];
		for(int i=0;i<caseNum;i++) {
			int A = sc.nextInt();int B = sc.nextInt();int C = sc.nextInt();int IO = sc.nextInt();
			arr[i][0] = A; arr[i][1] = B; arr[i][2] = C; arr[i][3] = IO;
		}
		return arr;
	}
	public static int[] makeRandom(String filename) throws FileNotFoundException {
		int[] random = new int[100000];
		FileReader file = new FileReader(filename);
		Scanner sc = new Scanner(file);
		int count =0;
		while(sc.hasNext()) {random[count]=sc.nextInt(); count++;}
		return random;
	}
	public static int randomOS(int U, int[]random, Machine_UNI Machine_UNI){
		Machine_UNI.randomPtr ++;
		return 1+(random[Machine_UNI.randomPtr] % U);

	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
 enum State_UNI{
	unstarted,
	running,
	blocked,
	terminated,
	ready

}

 class Process_UNI implements Comparable<Process_UNI>{
	int name;
	int Arrival;//A
	int cpuBurst;//B
	int CPUtime;//C
	int IOBurst;//D
	int CPUTimeRemain;
	int finishTime;
	int IOtime;
	int waitingTime;
	int turnAroundTime;
	boolean isReady = false;
	State_UNI State_UNI;
	//variable that records Process_UNI completion
	int runFor;
	int blockedFor;

	public String toString() {
		return this.name+" ";
	}

	Process_UNI(int name, int A, int B, int C,int IO){
		this.name = name;
		this.Arrival = A;
		this.cpuBurst = B;
		this.CPUtime = C;
		this.IOBurst = IO;
		this.State_UNI = State_UNI.unstarted;
	}
	boolean finishBlock() {
		if (this.blockedFor ==0) {
			return true;
		}
		else
			return false;
	}
	public void printProcess() {

		System.out.printf("Process_UNI %d:\n", this.name);
		System.out.printf("          (A,B,C,IO) = (%d,%d,%d,%d)\n", this.Arrival, this.cpuBurst, this.CPUtime,this.IOBurst);
		System.out.printf("          Finishing time: %d\n", this.finishTime);
		System.out.printf("          Turnaround time: %d\n", this.finishTime - this.Arrival);
		System.out.printf("          I/O time: %d\n", this.IOtime);
		System.out.printf("          Waiting time: %d\n\n", this.waitingTime);
	}



	@Override
	public int compareTo(Process_UNI o) {
		// TODO Auto-generated method stub
		return this.Arrival - o.Arrival;
	}

}

 class Machine_UNI {
	 int processDone;
	 int cycleCount =0;
	 int arrivedProcess=0;
	 int finishTime;
	 double CPU_Uti;
	 double IO_Uti;
	 double turnaroundTotal;
	 double waitTotal;
	 public int randomPtr=-1;
	 ArrayList<Process_UNI> ReadyQ = new ArrayList<Process_UNI>();
	 ArrayList<Process_UNI> BlockedQ = new ArrayList<Process_UNI>();
	 ArrayList<Process_UNI> RunningQ = new ArrayList<Process_UNI>();
	 ArrayList<Process_UNI> ArriveQ = new ArrayList<Process_UNI>();
	 ArrayList<Process_UNI> tray = new ArrayList<Process_UNI>();
	 ArrayList<Process_UNI> Working = new ArrayList<Process_UNI>();
	 ArrayList<Process_UNI> PArr = new ArrayList<Process_UNI>();

	 public void printSummary(){
		 System.out.printf("Summary Data: \n");
			System.out.printf("          Finishing time: %d\n", this.finishTime);
			System.out.printf("          CPU Utilization: %f\n", this.CPU_Uti/this.finishTime);
			System.out.printf("          I/O Utilization: %f\n", this.IO_Uti/this.finishTime);
			System.out.printf("          Throughput: %f processes per hundred cycles \n", (double)this.PArr.size()/this.finishTime*100);
			System.out.printf("          Average turnaround time: %f\n", this.turnaroundTotal/this.PArr.size());
			System.out.printf("          Average waiting time: %f\n", this.waitTotal/this.PArr.size());
	 }
 }
