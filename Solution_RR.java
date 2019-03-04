import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Solution_RR {

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


		Machine_RR M = new Machine_RR();// make a new Machine_RR
		// add every Process_RR to the Machine_RR's arrayList
		for(int i=0;i<schedule.length;i++) {
			Process_RR p = new Process_RR(i, schedule[i][0],schedule[i][1],schedule[i][2],schedule[i][3]);
			p.CPUTimeRemain = p.CPUtime;
			M.PArr.add(p);
		}

		M.processDone = M.PArr.size();
		if(flag) {
			System.out.println("\nThis detailed printout gives the State_RR and remaining burst for each Process_RR\n");
		}
		while(M.processDone>0) {

			if(true) {
				if(flag) {
					System.out.printf("Before cycle%5d:   ", M.cycleCount);}
				int forhowlong =0;
				for(Process_RR p:M.PArr) {
					switch(p.State_RR) {
					case running:
						forhowlong = p.actualRunfor;
						break;
					case blocked:forhowlong = p.blockedFor; p.IOtime++;

					break;
					case unstarted: forhowlong = 0;
					break;
					case terminated: forhowlong = 0;
					break;
					case ready:
						forhowlong = 0;
						p.waitingTime++;

					}
					if(flag) {
						System.out.printf("%10s  %2d  ",p.State_RR,forhowlong );}
					//					forhowlong --;
					//System.out.println("CPU remain"+p.CPUTimeRemain);

				}
				//				System.out.print(".");
				if(flag) {
					System.out.println();}

			}
			doBlocked(M, random);
			doRunning(M, random);
			addToReady(M);
			doArrive(M);
			doReady(M,random);
			M.cycleCount++;
		}
		System.out.println("\nThe scheduling algorithm used was Round Robbin\n");

		for(Process_RR p:M.PArr) {
			p.printProcess();
		}

		int finish = 0;
		for(Process_RR p:M.PArr) {
			M.IO_Uti +=p.IOtime;
			M.CPU_Uti += p.CPUtime;
			M.turnaroundTotal += p.finishTime - p.Arrival;
			M.waitTotal += p.waitingTime;
			if(p.finishTime>finish) {
				finish = p.finishTime;
			}

		}
		M.finishTime = finish;
		M.printSummary();
	}
	//add to sort from do block and
	public static void doBlocked(Machine_RR M,int[] random) {
		if(M.BlockedQ.isEmpty()) {
			return;
		}
		ArrayList<Process_RR> toRemove = new ArrayList<Process_RR>();
		ArrayList<Process_RR> toAdd = new ArrayList<Process_RR>();
		for(int i=0;i<M.BlockedQ.size();i++) {
			Process_RR p = M.BlockedQ.get(i);
			p.blockedFor --;
			p.State_RR = State_RR.blocked;
			if(p.blockedFor<=0) {
				toRemove.add(p);
				toAdd.add(p);
				M.tray.add(p);
				p.State_RR = State_RR.ready;
			}
		}
		M.BlockedQ.removeAll(toRemove);
		Collections.sort(toAdd,Process_RR.CompareByName);

	}

	public static void addToReady(Machine_RR M) {
		Collections.sort(M.tray, Process_RR.CompareByName);
		M.ReadyQ.addAll(M.tray);
		M.tray.clear();
	}
	public static void doRunning(Machine_RR M, int[] random) {
		if(M.RunningQ.isEmpty()) {
			return;
		}
		Process_RR p = M.RunningQ.get(0);
		p.State_RR = State_RR.running;
		p.runFor--;
		p.CPUTimeRemain --;
		p.preempts --;
		p.actualRunfor--;

		if(p.CPUTimeRemain <=0) {
			p.State_RR = State_RR.terminated;
			p.finishTime = M.cycleCount;
			M.RunningQ.remove(p);
			M.processDone --;
			return;
		}

		if(p.runFor ==0 ) {
			p.preempts =2;
			M.RunningQ.remove(p);
			M.BlockedQ.add(p);
			p.State_RR = State_RR.blocked;
			p.blockedFor = randomOS(p.IOBurst, random,M);
			p.fromWaiting = false;
		}
		else if(p.preempts ==0) {
			p.preempts = 2;
			M.RunningQ.remove(p);
			p.State_RR = State_RR.ready;////////sort
			if(p.runFor>=2) {
				p.actualRunfor = 2;}
			else {
				p.actualRunfor=1;
			}
			p.fromWaiting = true;
			M.tray.add(p);
		}
	}
	public static void doArrive(Machine_RR M) {
		if(M.arrivedProcess == M.PArr.size()) {
			return;
		}
		for(int i=0;i<M.PArr.size();i++) {
			Process_RR p = M.PArr.get(i);
			if(p.Arrival == M.cycleCount) {
				M.ReadyQ.add(p);
				p.State_RR = State_RR.ready;
				//p.actualRunfor = 2;
				M.arrivedProcess++;
			}
		}
	}
	public static void doReady(Machine_RR M, int[] random) {
		if(M.ReadyQ.isEmpty()) {
			return;
		}
		Process_RR p = M.ReadyQ.get(0);
		if(M.RunningQ.isEmpty() ) {
			M.RunningQ.add(p);
			p.State_RR= State_RR.running;
			M.ReadyQ.remove(p);
			if(!p.fromWaiting) {
				p.runFor = randomOS(p.cpuBurst, random,M);
				///????
				if(p.runFor>=2) {
					p.actualRunfor =2;
				}
				else {
					p.actualRunfor = 1;
				}

			}
			//
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
	public static int randomOS(int U, int[]random, Machine_RR Machine_RR){
		Machine_RR.randomPtr ++;
		return 1+(random[Machine_RR.randomPtr] % U);

	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
enum State_RR{
	unstarted,
	running,
	blocked,
	terminated,
	ready
}
class Process_RR implements Comparable<Process_RR>{
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
	int preempts =2;
	int actualRunfor;



	boolean fromWaiting = false;
	boolean isReady = false;
	State_RR State_RR ;
	//variable that records Process_RR completion
	int runFor;
	int blockedFor;

	public String toString() {
		return this.name+" ";
	}

	Process_RR(int name, int A, int B, int C,int IO){
		this.name = name;
		this.Arrival = A;
		this.cpuBurst = B;
		this.CPUtime = C;
		this.IOBurst = IO;
		this.State_RR = State_RR.unstarted;
	}
	boolean finishBlock() {
		if (this.blockedFor ==0) {
			return true;
		}
		else
			return false;
	}
	public void printProcess() {
		System.out.printf("Process_RR %d:\n", this.name);
		System.out.printf("          (A,B,C,IO) = (%d,%d,%d,%d)\n", this.Arrival, this.cpuBurst, this.CPUtime,this.IOBurst);
		System.out.printf("          Finishing time: %d\n", this.finishTime);
		System.out.printf("          Turnaround time: %d\n", this.finishTime - this.Arrival);
		System.out.printf("          I/O time: %d\n", this.IOtime);
		System.out.printf("          Waiting time: %d\n\n", this.waitingTime);
	}
	@Override
	public int compareTo(Process_RR o) {
		// TODO Auto-generated method stub
		return this.Arrival - o.Arrival;
	}
	public static Comparator<Process_RR> CompareByName = new Comparator<Process_RR>() {
		@Override
		public int compare(Process_RR o1, Process_RR o2) {
			// TODO Auto-generated method stub
			return o1.name - o2.name;
		}
	};
}
class Machine_RR {
	int processDone;
	int cycleCount =0;
	int arrivedProcess=0;
	int finishTime;
	double CPU_Uti;
	double IO_Uti;
	double turnaroundTotal;
	double waitTotal;
	public int randomPtr=-1;
	ArrayList<Process_RR> ReadyQ = new ArrayList<Process_RR>();
	ArrayList<Process_RR> BlockedQ = new ArrayList<Process_RR>();
	ArrayList<Process_RR> RunningQ = new ArrayList<Process_RR>();
	ArrayList<Process_RR> ArriveQ = new ArrayList<Process_RR>();
	ArrayList<Process_RR> tray = new ArrayList<Process_RR>();
	ArrayList<Process_RR> Working = new ArrayList<Process_RR>();
	ArrayList<Process_RR> PArr = new ArrayList<Process_RR>();
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
