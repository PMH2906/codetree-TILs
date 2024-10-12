import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder output=new StringBuilder();
	static StringTokenizer tokens;
	static int N, M, tern, passCnt=0;
	static int[][] map, deltas= {{-1,0},{0,-1},{0,1},{1,0}};
	static boolean[][] isNonpass;
	static Stroe[] stroes;
	static People[] peoples;
	
	static public class Stroe {
		int x, y;

		public Stroe(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
		
	}
	
	static public class People {
		int x, y;
		boolean pass=false;

		public People(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "People [x=" + x + ", y=" + y + ", pass=" + pass + "]";
		}
	}

	
	static public class MovePoint implements Comparable<MovePoint>{
		int x, y, dist;

		public MovePoint(int x, int y, int dist) {
			super();
			this.x = x;
			this.y = y;
			this.dist = dist;
		}

		@Override
		public int compareTo(MovePoint o) {
//			if(this.dist==o.dist) {
//				return Integer.compare(this.direct,o.direct);
//			}
			return Integer.compare(this.dist,o.dist);
		}
	}
	
	static public class Point implements Comparable<Point>{
		int x, y, dist;

		public Point(int x, int y, int dist) {
			super();
			this.x = x;
			this.y = y;
			this.dist = dist;
		}

		@Override
		public int compareTo(Point o) {
			if(this.dist==o.dist) {
				if(this.x==o.x) {
					return Integer.compare(this.y, o.y);
				}
				return Integer.compare(this.x, o.x);
			}
			return Integer.compare(this.dist,o.dist);
		}
	}
	
	public static void main(String[] args) throws IOException {
		tokens=new StringTokenizer(input.readLine());
		
		N=Integer.parseInt(tokens.nextToken());
		M=Integer.parseInt(tokens.nextToken());
		
		// 초기화
		map=new int[N][N];
		isNonpass=new boolean[N][N];
		stroes=new Stroe[M+1];
		peoples=new People[M+1];
		tern=0;
		
		for(int r=0;r<N;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<N;c++) {
				map[r][c]=Integer.parseInt(tokens.nextToken());
			}
		}
		
		for(int m=1;m<=M;m++) {
			tokens=new StringTokenizer(input.readLine());
			int x=Integer.parseInt(tokens.nextToken())-1;
			int y=Integer.parseInt(tokens.nextToken())-1;
			stroes[m]=new Stroe(x, y);
		}
		
		while(true) {
			tern+=1;
			
			move(tern);
			
//			System.out.println(tern+"움직임 -");
			for(int m=1;m<=Math.min(M, tern-1);m++) {
//				System.out.println(peoples[m].toString());
				if(peoples[m].pass) continue;
				if(peoples[m].x==stroes[m].x&&peoples[m].y==stroes[m].y) {
					isNonpass[peoples[m].x][peoples[m].y]=true;
					peoples[m].pass=true;
					passCnt+=1;
				}
			}
			
//			System.out.println(tern+"----------------------------");
//			for(int r=0;r<N;r++) {
//				System.out.println(Arrays.toString(isNonpass[r]));
//			}
//			
			if(passCnt==M) break;
			
			if(tern<=M) enter(tern);
			
//			System.out.println(tern+"----------------------------");
//			for(int r=0;r<N;r++) {
//				System.out.println(Arrays.toString(isNonpass[r]));
//			}
//			System.out.println(tern+"들어감");
//			for(int m=1;m<=Math.min(M, tern);m++) {
//				System.out.println(peoples[m].toString());
//			}
		}
		output.append(tern);
		
		System.out.print(output);
	}


	private static void enter(int tern) {
		PriorityQueue<Point> pq=new PriorityQueue<>();
		boolean[][] visited=new boolean[N][N];
		
		pq.add(new Point(stroes[tern].x, stroes[tern].y, 0));
		visited[stroes[tern].x][stroes[tern].y]=true;
		
		while(pq.size()>0) {
			Point now=pq.poll();
			
			// 베이스 캠프 선택 
			if(map[now.x][now.y]==1) {
				peoples[tern]=new People(now.x,now.y);
				isNonpass[now.x][now.y]=true;
				return;
			}
			
			for(int d=0;d<deltas.length;d++) {
				int nx=now.x+deltas[d][0];
				int ny=now.y+deltas[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				
				// 다음 이동 위치 선택 
				if(!isNonpass[nx][ny]&&!visited[nx][ny]) {
					pq.add(new Point(nx, ny, now.dist+1));
					visited[nx][ny]=true;
				}
			}
		}
	}


	private static void move(int tern) {
		for(int m=1;m<=Math.min(M, tern-1);m++) {
			if(peoples[m].pass) continue;
			
			int minDist=Integer.MAX_VALUE;
			int minDistDirect=Integer.MAX_VALUE;
			
			for(int d=0;d<deltas.length;d++) {
				PriorityQueue<MovePoint> pq=new PriorityQueue<>();
				boolean[][] visited=new boolean[N][N];
				
				int nextNx=peoples[m].x+deltas[d][0];
				int nextNy=peoples[m].y+deltas[d][1];
				
				if(nextNx<0||nextNx>=N||nextNy<0||nextNy>=N) continue;
				if(isNonpass[nextNx][nextNy]) continue;
				
				pq.add(new MovePoint(nextNx, nextNy, 0));
				visited[nextNx][nextNy]=true;
				
				while(pq.size()>0) {
					MovePoint now=pq.poll();
					
					if(stroes[m].x==now.x&&stroes[m].y==now.y) {
						if(minDist>now.dist) {
							minDistDirect=d;
							minDist=now.dist;
						}
						continue;
					}
					
					for(int innerD=0;innerD<deltas.length;innerD++) {
						int nx=now.x+deltas[innerD][0];
						int ny=now.y+deltas[innerD][1];
						
						if(nx<0||nx>=N||ny<0||ny>=N) continue;
						
						// 다음 이동 위치 선택 
						if(!isNonpass[nx][ny]&&!visited[nx][ny]) {
							pq.add(new MovePoint(nx, ny, now.dist+1));
							visited[nx][ny]=true;
						}
					}
				}
			}

			peoples[m].x+=deltas[minDistDirect][0];
			peoples[m].y+=deltas[minDistDirect][1];
			
		}
	}
	
	
}