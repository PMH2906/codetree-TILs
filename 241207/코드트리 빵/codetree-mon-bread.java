import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

// M명, N*N
public class Main {
    static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
    static StringBuilder output=new StringBuilder();
    static StringTokenizer tokens;
    static int N, M, time, removedCnt;
    static int[][] map, deltas= {{-1,0},{0,-1},{0,1},{1,0}}; // 상좌우하  
    static boolean[][] pass;
    static People[] peoples;
    
    static class People {
    	int x, y, comX, comY, baseX, baseY;
    	boolean removed;

		public People(int comX, int comY) {
			super();
			this.comX = comX;
			this.comY = comY;
		}

		@Override
		public String toString() {
			return "People [x=" + x + ", y=" + y + ", comX=" + comX + ", comY=" + comY + ", baseX=" + baseX + ", baseY="
					+ baseY + ", removed=" + removed + "]";
		}
		
    }
	public static void main(String[] args) throws IOException {
		
		tokens=new StringTokenizer(input.readLine());
		
		N=Integer.parseInt(tokens.nextToken());
		M=Integer.parseInt(tokens.nextToken());
		
		map=new int[N][N];
		pass=new boolean[N][N];
		peoples=new People[M];
		
		for(int r=0;r<N;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<N;c++) {
				map[r][c]=Integer.parseInt(tokens.nextToken());
			}
		}
		
		for(int m=0;m<M;m++) {
			tokens=new StringTokenizer(input.readLine());
			int x=Integer.parseInt(tokens.nextToken())-1;
			int y=Integer.parseInt(tokens.nextToken())-1;
			peoples[m]=new People(x, y);
		}
		
		time=-1;
		removedCnt=0;
		while(removedCnt<M) {
			time+=1;
			
			move();
			
			// 2. 편의점 도착 시 해당 편의점 지나갈 수 없음
			for(int m=0;m<Math.min(time, M);m++) {
				if(peoples[m].removed) continue;
				
				if(peoples[m].x==peoples[m].comX&&peoples[m].y==peoples[m].comY) {
					peoples[m].removed=true;
					removedCnt+=1;
					pass[peoples[m].x][peoples[m].y]=true;
				}
			}
			
			if(time<M) enter();

						
//			for(int r=0;r<N;r++) System.out.println(Arrays.toString(map[r]));
//			System.out.println();
		}
		output.append(time+1);
		System.out.println(output);
	}
	
	private static void move() {
		loop: for(int m=0;m<Math.min(time, M);m++) {
//			System.out.println(peoples[m].toString());
			if(peoples[m].removed) continue;
			
			Queue<int[]> q=new LinkedList<>(); // x,y,dir;
			boolean[][] visited=new boolean[N][N];
			q.add(new int[] {peoples[m].x, peoples[m].y, -1});
			visited[peoples[m].x][peoples[m].y]=true;
			
			while(q.size()>0) {
				
				int[] now=q.poll();
				
				// 편의점 도착 시 
				if(now[0]==peoples[m].comX&&now[1]==peoples[m].comY) {
					peoples[m].x+=deltas[now[2]][0];
					peoples[m].y+=deltas[now[2]][1];
//					System.out.println(peoples[m].toString());
					continue loop;
				}
				
				// 못 지나가는 곳 체크하기 
				for(int d=0;d<4;d++) {
					int nx=now[0]+deltas[d][0];
					int ny=now[1]+deltas[d][1];
					
					if(nx<0||nx>=N||ny<0||ny>=N) continue;
					
					if(!pass[nx][ny]&&!visited[nx][ny]) {
						if(now[2]==-1) q.add(new int[] {nx, ny, d});
						else q.add(new int[] {nx, ny, now[2]});
						visited[nx][ny]=true;
					}
				}
				
			}
		}
		
	}

	// 3.t<=m 만족 시 t번 사람은 편의점과 가까운 베이스캠프 입장, 상,하,좌,우 최단 거리 베이스캠프. r 작, c작 
	// 		해당 베이스 캠프는 지나갈 수 없음
	private static void enter() {
		PriorityQueue<int[]> pq=new PriorityQueue<int[]>(new Comparator<int[]>() {
			
			// x, y, cnt
			@Override
			public int compare(int[] o1, int[] o2) {
				if(o1[2]==o2[2]) {
					if(o1[0]==o2[0]) {
						return Integer.compare(o1[1], o2[1]);
					}
					return Integer.compare(o1[0], o2[0]);
				}
				return Integer.compare(o1[2], o2[2]);
			}
		});
		boolean[][] visted=new boolean[N][N];
		pq.add(new int[] {peoples[time].comX, peoples[time].comY,0});
		visted[peoples[time].comX][peoples[time].comY]=true;
		
		while(pq.size()>0) {
			int[] now=pq.poll();
			
			// 베이스캠프 찾기 
			if(map[now[0]][now[1]]==1) {
				peoples[time].baseX=now[0];
				peoples[time].baseY=now[1];
				peoples[time].x=now[0];
				peoples[time].y=now[1];
				pass[now[0]][now[1]]=true; // 못 지나감 
				map[now[0]][now[1]]=0; // 베이스캠프 제거 
				return;
			}
			
			for(int d=0;d<4;d++) {
				int nx=now[0]+deltas[d][0];
				int ny=now[1]+deltas[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				
				if(!pass[nx][ny]&&!visted[nx][ny]) {
					pq.add(new int[] {nx,ny,now[2]+1});
					visted[nx][ny]=true;
				} 
			}
		}
	}

}
