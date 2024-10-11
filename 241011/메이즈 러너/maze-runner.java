import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {
	
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder output=new StringBuilder();
	static StringTokenizer tokens;
	static int N, M, K, passCnt, ans;
	static int[][] map, deltas= {{-1,0},{1,0},{0,-1},{0,1}};
	static boolean[][][] mapNumPeople;
	static People[] peoples;
	static int[] gate;
	
	public static class People{
		int x, y;
		boolean pass;

		public People(int x, int y, boolean pass) {
			super();
			this.x = x;
			this.y = y;
			this.pass=pass;
		}

		@Override
		public String toString() {
			return "People [x=" + x + ", y=" + y + ", pass=" + pass + "]";
		}
		
	}
	public static class Move implements Comparable<Move>{
		int x, y, dist;

		public Move(int x, int y, int dist) {
			super();
			this.x = x;
			this.y = y;
			this.dist = dist;
		}

		@Override
		public int compareTo(Move o) {
			return Integer.compare(this.dist, o.dist);
		}
	}
	
	public static class MinRowCol implements Comparable<MinRowCol>{
		int size, x, y;

		public MinRowCol(int gateX, int gateY, int x, int y) {
			super();
			this.size = Math.max(Math.abs(gateX-x),Math.abs(gateY-y))+1;
			this.x = Math.max(Math.max(gateX, x)-(this.size-1),0);
			this.y = Math.max(Math.max(gateY, y)-(this.size-1),0);
		}

		@Override
		public int compareTo(MinRowCol o) {
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
		
		map=new int[N][N];
		mapNumPeople=new boolean[N][N][M+1];
		peoples=new People[M+1];
		gate=new int[2];
		ans=0;
		
		for(int r=0;r<N;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<N;c++) {
				map[r][c]=Integer.parseInt(tokens.nextToken());
			}
		}
		
		int x, y;
		for(int m=1;m<=M;m++) {
			tokens=new StringTokenizer(input.readLine());
			x=Integer.parseInt(tokens.nextToken())-1;
			y=Integer.parseInt(tokens.nextToken())-1;
			peoples[m]=new People(x, y, false);
			mapNumPeople[x][y][m]=true;
		}
		
		tokens=new StringTokenizer(input.readLine());
		x=Integer.parseInt(tokens.nextToken())-1;
		y=Integer.parseInt(tokens.nextToken())-1;
		map[x][y]=-1;
		gate[0]=x;
		gate[1]=y;
		
		for(int k=0;k<K;k++) {
	
			if(passCnt==M) break;
			
			movePeople();

			if(passCnt==M) break;
			
			rotate90();
		}
		
		output.append(ans+"\n");
		output.append((gate[0]+1)+" "+(gate[1]+1));
		
		System.out.println(output);
	}

	public static void rotate90() {
		PriorityQueue<MinRowCol> pq=new PriorityQueue<>();
		int[][] newMap=new int[N][N];
		boolean[][][] newPeopleNum=new boolean[N][N][M+1];
		for(int m=1;m<=M;m++) {
			if(peoples[m].pass) continue;
			
			pq.add(new MinRowCol(gate[0],gate[1],peoples[m].x,peoples[m].y));
		}
		
		MinRowCol minRowCol=pq.poll();
		
		// 회전 
		for(int r=minRowCol.x;r<minRowCol.x+minRowCol.size;r++) {
			for(int c=minRowCol.y;c<minRowCol.y+minRowCol.size;c++) {
				int temp=map[minRowCol.x+(minRowCol.size-1)-(c-minRowCol.y)][minRowCol.y+(r-minRowCol.x)];
				if(temp==-1) {
					newMap[r][c]=temp;
					gate[0]=r;
					gate[1]=c;
				} else {
					newMap[r][c]=temp-1<=0?0:temp-1;
				}
				for(int m=1;m<=M;m++) {
					boolean tempBoolean=mapNumPeople[minRowCol.x+(minRowCol.size-1)-(c-minRowCol.y)][minRowCol.y+(r-minRowCol.x)][m];
					if(tempBoolean) {
						peoples[m].x=r;
						peoples[m].y=c;
					}
					newPeopleNum[r][c][m]=tempBoolean;
				}
			}
		}
		
		// 새로운 map 셋팅 
		for(int r=minRowCol.x;r<minRowCol.x+minRowCol.size;r++) {
			for(int c=minRowCol.y;c<minRowCol.y+minRowCol.size;c++) {	
				map[r][c]=0;
				map[r][c]=newMap[r][c];
				for(int m=1;m<=M;m++) {
					mapNumPeople[r][c][m]=false;
					mapNumPeople[r][c][m]=newPeopleNum[r][c][m];
				}
			}
		}
	}

	public static void movePeople() {
		
		for(int m=1;m<=M;m++) {
			if(peoples[m].pass) continue;
			
			PriorityQueue<Move> pq=new PriorityQueue<>();
			for(int d=0;d<deltas.length;d++) {
				int nx=peoples[m].x+deltas[d][0];				
				int ny=peoples[m].y+deltas[d][1];
				
				int dist=Math.abs(gate[0]-nx)+Math.abs(gate[1]-ny);
				int originDist=Math.abs(gate[0]-peoples[m].x)+Math.abs(gate[1]-peoples[m].y);
				
				if(originDist>dist&&map[nx][ny]<=0) {
					pq.add(new Move(nx, ny, dist));
				}
			}
			
			// 움직일 곳 없으면 종료 
			if(pq.size()==0) continue;
			
			Move move=pq.poll();
			ans+=1;
			
			// 출구면 종료 
			if(map[move.x][move.y]==-1) {
				mapNumPeople[peoples[m].x][peoples[m].y][m]=false;
				peoples[m].pass=true;
				passCnt+=1;
				continue;
			}
			
			// 이동 
			mapNumPeople[peoples[m].x][peoples[m].y][m]=false;
			peoples[m].x=move.x;
			peoples[m].y=move.y;
			mapNumPeople[move.x][move.y][m]=true;
		}
	} 
}