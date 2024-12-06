import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringTokenizer tokens;
	static StringBuilder output=new StringBuilder();
	static int N, M, K, removedCnt, ans;
	static int [][] map, deltas= {{-1,0},{1,0},{0,-1},{0,1}};
	static boolean[][][] num;
	static People[] peoples;
	static int[] gate;
	static class People {
		int x, y;
		boolean removed;
		
		public People(int x, int y, boolean removed) {
			super();
			this.x = x;
			this.y = y;
			this.removed = removed;
		}
	}
	static class Info implements Comparable<Info>{
		int x, y, size;

		public Info(int x, int y) {
			super();
			this.size = Math.max(Math.abs(gate[0]-x),Math.abs(gate[1]-y))+1;
			this.x = Math.max(Math.max(gate[0],x)-(size-1), 0);
			this.y = Math.max(Math.max(gate[1],y)-(size-1), 0);
		}

		@Override
		public int compareTo(Info o) {
			if(this.size==o.size) {
				if(this.x==o.x) {
					return Integer.compare(this.y, o.y);
				}
				return Integer.compare(this.x, o.x);
			}
			return Integer.compare(this.size, o.size);
		}
		
	}
	public static void main(String[] args) throws IOException {
		
		tokens=new StringTokenizer(input.readLine());
		N=Integer.parseInt(tokens.nextToken());
		M=Integer.parseInt(tokens.nextToken());
		K=Integer.parseInt(tokens.nextToken());
		
		removedCnt=0;
		ans=0;
		map=new int[N][N];
		peoples=new People[M+1];
		gate=new int[2];
		num=new boolean[M+1][N][N];
	
		for(int r=0;r<N;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<N;c++) {
				map[r][c]=Integer.parseInt(tokens.nextToken());
			}
		}
		
		for(int m=1;m<=M;m++) {
			tokens=new StringTokenizer(input.readLine());
			int x= Integer.parseInt(tokens.nextToken())-1;
			int y= Integer.parseInt(tokens.nextToken())-1;
			
			peoples[m]=new People(x, y, false);
			num[m][x][y]=true;
		}
		
		tokens=new StringTokenizer(input.readLine());
		gate[0]=Integer.parseInt(tokens.nextToken())-1;
		gate[1]=Integer.parseInt(tokens.nextToken())-1;
		map[gate[0]][gate[1]]=-1;
		
		for(int k=0;k<K;k++) {
			
			move();
			if(removedCnt==K) break;
			
			rotate();
			
//			for(int r=0;r<N;r++) System.out.println(Arrays.toString(map[r]));
//			System.out.println();
		}
		output.append(ans+"\n"+(gate[0]+1)+" "+(gate[1]+1));
		System.out.println(output);
	}
	private static void rotate() {
		
		PriorityQueue<Info> pq=new PriorityQueue<>();
		for(int m=1;m<=M;m++) {
			if(peoples[m].removed) continue;
			pq.add(new Info(peoples[m].x, peoples[m].y));
		}
		
		// 가장 작은 정사각형 정보 꺼내기 
		Info info=pq.poll();
		int[][] temp=new int[info.size][info.size];
		boolean[][][] tempNum=new boolean[M+1][info.size][info.size];
		
		// 90도 회전 
		for(int r=0;r<info.size;r++) {
			for(int c=0;c<info.size;c++) {
				temp[r][c]=map[info.x+(info.size-1)-c][info.y+r];
				for(int m=1;m<=M;m++) tempNum[m][r][c]=num[m][info.x+(info.size-1)-c][info.y+r];
			}
		}
		
		for(int r=0;r<info.size;r++) {
			for(int c=0;c<info.size;c++) {
				
				if(temp[r][c]>0) temp[r][c]-=1; 
				map[info.x+r][info.y+c]=temp[r][c];
				
				// 회전한 출구 정보 갱신 
				if(map[info.x+r][info.y+c]==-1) {
					gate[0]=info.x+r;
					gate[1]=info.y+c;
				}
				
				
				for(int m=1;m<=M;m++) {
					
					// 회전한 후 존재하는 사람 정보 
					num[m][info.x+r][info.y+c]=tempNum[m][r][c];
					
					// 회전에 포함한 사람 정보 갱신 
					if(num[m][info.x+r][info.y+c]==true) {
						peoples[m].x=info.x+r;
						peoples[m].y=info.y+c;
					}
				}
			}
		}
		
	}
	private static void move() {
		
		for(int m=1;m<=M;m++) {
			
			if(peoples[m].removed) continue;
			int dist=Math.abs(gate[0]-peoples[m].x)+Math.abs(gate[1]-peoples[m].y);
			int dir=-1;
			for(int d=0;d<4;d++) {
				int nx=peoples[m].x+deltas[d][0];
				int ny=peoples[m].y+deltas[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				if(map[nx][ny]>0) continue;
			
				int nextDist=Math.abs(gate[0]-nx)+Math.abs(gate[1]-ny);
				
				if(dist>nextDist) {
					dir=d;
					dist=nextDist;
				}
			}
			
			if(dir==-1) continue;
			ans+=1;
			num[m][peoples[m].x][peoples[m].y]=false;
			
			if(dist==0) {
				peoples[m].removed=true;
				removedCnt+=1;
				continue;
			}
			peoples[m].x+=deltas[dir][0];
			peoples[m].y+=deltas[dir][1];
			num[m][peoples[m].x][peoples[m].y]=true;
			
		}
		
	}

}
