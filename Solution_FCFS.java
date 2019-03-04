import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Solution_FCFS {

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


		Machine_FCFS M = new Machine_FCFS();// make a new Machine_FCFS
		// add every Process_FCFS to the Machine_FCFS's arrayList
		for(int i=0;i<schedule.length;i++) {
			Process_FCFS p = new Process_FCFS(i, schedule[i][0],schedule[i][1],schedule[i][2],schedule[i][3]);
			p.CPUTimeRemain = p.CPUtime;
			M.PArr.add(p);
		}

		M.processDone = M.PArr.size();
		if(flag) {
		System.out.println("\nThis detailed printout gives the State_FCFS and remaining burst for each Process_FCFS\n");
		}
		while(M.processDone>0) {

			if(true) {
				if(flag) {
				System.out.printf("Before cycle%5d:   ", M.cycleCount);}
				int forhowlong =0;
				for(Process_FCFS p:M.PArr) {
					switch(p.State_FCFS) {
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
					System.out.printf("%10s  %2d  ",p.State_FCFS,forhowlong);}
					//System.out.println("CPU remain"+p.CPUTimeRemain);
				}
			if(flag) {
			System.out.println();}}


			doBlocked(M, random);
			doRunning(M, random);
			doArrive(M);
			doReady(M,random);
			M.cycleCount++;

		}
		System.out.println("\nThe scheduling algorithm used was First Come First Serve\n");

		for(Process_FCFS p:M.PArr) {
			p.printProcess();
		}

		int finish = 0;
		for(Process_FCFS p:M.PArr) {
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
	public static void doBlocked(Machine_FCFS M,int[] random) {
		if(M.BlockedQ.isEmpty()) {
			return;
		}
//		Process_FCFS p = M.BlockedQ.get(0);
//		p.blockedFor --;
		ArrayList<Process_FCFS> toRemove = new ArrayList<Process_FCFS>();
		ArrayList<Process_FCFS> toAdd = new ArrayList<Process_FCFS>();
		for(int i=0;i<M.BlockedQ.size();i++) {
			Process_FCFS p = M.BlockedQ.get(i);
			p.blockedFor --;
			p.State_FCFS = State_FCFS.blocked;
			if(p.blockedFor<=0) {
				//System.out.println("swtich to ready");
				toRemove.add(p);
				toAdd.add(p);
				//M.ReadyQ.add(p);
				p.State_FCFS = State_FCFS.ready;
			}
		}
		M.BlockedQ.removeAll(toRemove);
		Collections.sort(toAdd,Process_FCFS.CompareByName);
		M.ReadyQ.addAll(toAdd);
		//should we sort the array?


		//p.CPUTimeRemain++; import!!!!
//		p.State_FCFS = State_FCFS.blocked;
//		if(p.blockedFor <=0) {
//			System.out.println("switch to ready");
//			M.BlockedQ.remove(p);
//			M.RunningQ.add(p);
//			p.State_FCFS = State_FCFS.running;
//			p.runFor = randomOS(p.cpuBurst, random,M)+1;
//
//		}
	}

	public static void doRunning(Machine_FCFS M, int[] random) {
		if(M.RunningQ.isEmpty()) {
			return;
		}
		Process_FCFS p = M.RunningQ.get(0);
		p.State_FCFS = State_FCFS.running;
		p.runFor--;
		p.CPUTimeRemain --;

		if(p.CPUTimeRemain <=0) {
			p.State_FCFS = State_FCFS.terminated;
			p.finishTime = M.cycleCount;
			M.RunningQ.remove(p);
			M.processDone --;
			return;
		}

		if(p.runFor ==0) {
			M.RunningQ.remove(p);
			M.BlockedQ.add(p);
			p.State_FCFS = State_FCFS.blocked;
			//p.CPUTimeRemain++;
			//System.out.print("Process_FCFS "+p+"is now blocked");
			p.blockedFor = randomOS(p.IOBurst, random,M);
		}
	}
	public static void doArrive(Machine_FCFS M) {
		if(M.arrivedProcess == M.PArr.size()) {
			return;
		}
		for(int i=0;i<M.PArr.size();i++) {
			Process_FCFS p = M.PArr.get(i);
			if(p.Arrival == M.cycleCount) {
				M.ReadyQ.add(p);
				p.State_FCFS = State_FCFS.ready;
				M.arrivedProcess++;
			}

		}
	}
	public static void doReady(Machine_FCFS M, int[] random) {
		if(M.ReadyQ.isEmpty()) {
			return;
		}
		Process_FCFS p = M.ReadyQ.get(0);
		if(M.RunningQ.isEmpty() ) {
			M.RunningQ.add(p);
			p.State_FCFS= State_FCFS.running;
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
	public static int randomOS(int U, int[]random, Machine_FCFS Machine_FCFS){
		Machine_FCFS.randomPtr ++;
		return 1+(random[Machine_FCFS.randomPtr] % U);

	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
 enum State_FCFS{
	unstarted,
	running,
	blocked,
	terminated,
	ready

}

 class Process_FCFS implements Comparable<Process_FCFS>{
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
	State_FCFS State_FCFS ;
	//variable that records Process_FCFS completion
	int runFor;
	int blockedFor;

	public String toString() {
		return this.name+" ";
	}

	Process_FCFS(int name, int A, int B, int C,int IO){
		this.name = name;
		this.Arrival = A;
		this.cpuBurst = B;
		this.CPUtime = C;
		this.IOBurst = IO;
		this.State_FCFS = State_FCFS.unstarted;
	}
	boolean finishBlock() {
		if (this.blockedFor ==0) {
			return true;
		}
		else
			return false;
	}
	public void printProcess() {

		System.out.printf("Process_FCFS %d:\n", this.name);
		System.out.printf("          (A,B,C,IO) = (%d,%d,%d,%d)\n", this.Arrival, this.cpuBurst, this.CPUtime,this.IOBurst);
		System.out.printf("          Finishing time: %d\n", this.finishTime);
		System.out.printf("          Turnaround time: %d\n", this.finishTime - this.Arrival);
		System.out.printf("          I/O time: %d\n", this.IOtime);
		System.out.printf("          Waiting time: %d\n\n", this.waitingTime);
	}



	@Override
	public int compareTo(Process_FCFS o) {
		// TODO Auto-generated method stub
		return this.Arrival - o.Arrival;
	}
	public static Comparator<Process_FCFS> CompareByName = new Comparator<Process_FCFS>() {
		@Override
		public int compare(Process_FCFS o1, Process_FCFS o2) {
			// TODO Auto-generated method stub
			return o1.name - o2.name;
		}

	};

}

 class Machine_FCFS {
	 int processDone;
	 int cycleCount =0;
	 int arrivedProcess=0;
	 int finishTime;
	 double CPU_Uti;
	 double IO_Uti;
	 double turnaroundTotal;
	 double waitTotal;
	 public int randomPtr=-1;
	 ArrayList<Process_FCFS> ReadyQ = new ArrayList<Process_FCFS>();
	 ArrayList<Process_FCFS> BlockedQ = new ArrayList<Process_FCFS>();
	 ArrayList<Process_FCFS> RunningQ = new ArrayList<Process_FCFS>();
	 ArrayList<Process_FCFS> ArriveQ = new ArrayList<Process_FCFS>();
	 ArrayList<Process_FCFS> tray = new ArrayList<Process_FCFS>();
	 ArrayList<Process_FCFS> Working = new ArrayList<Process_FCFS>();
	 ArrayList<Process_FCFS> PArr = new ArrayList<Process_FCFS>();

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
