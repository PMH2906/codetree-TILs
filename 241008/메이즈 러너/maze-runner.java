import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

/**
 * 주의. 출구와 참가자의 거리가 가장 가까운 이동 위치 중 벽이 존재하면 다른 이동 위치를 탐색해야 됨.(하지만 현재 이동 위치의 dist보다 작으면 탐색X)
 * 어려운 점. 정사각형의 가장 좌측 x,y 좌표 찾기(67line참고) 
 * 어려운 점. 90도 회전 (160line)
 * */
public class Main {
	
	static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder output=new StringBuilder();
	static StringTokenizer tokens;
	static int N, M, K, removeCnt=0, totalmoveCnt=0;
	static int[][] map, deltas= {{-1,0},{1,0},{0,-1},{0,1}};
	static int[][][] peopleMap;
	static People[] peopleList;
	static int[] gate=new int[2];

	public static class People{
		int x, y;
		boolean removed;
		
		public People(int x, int y, boolean removed) {
			super();
			this.x = x;
			this.y = y;
			this.removed = removed;
		}
	}
	public static class Info implements Comparable<Info>{
		int x, y, dist;
		
		public Info(int x, int y, int dist) {
			super();
			this.x = x;
			this.y = y;
			this.dist = dist;
		}

		@Override
		public int compareTo(Info o) {
			return Integer.compare(this.dist, o.dist);
		}
	}
	
	// 가장 작은 정사각형의 좌측 좌표 찾기 
	// (1) 가장 작은 크기를 갖는 정사각형이 2개 이상이라면, 
	// (2) 좌상단 r 좌표가 작은 것이 우선되고, 
	// (3) 그래도 같으면 c 좌표가 작은 것이 우선됩니다.
	public static class SmallPoint implements Comparable<SmallPoint>{
		int minX, minY, size;

		public SmallPoint(int x, int y, int[] gate) {
			super();
			this.size=Math.max(Math.abs(gate[0]-x), Math.abs(gate[1]-y))+1;	
			// 출구와 참가자의 x,y좌표 중 작은 x,y좌표에 size-1만큼 빼주기 
			this.minX=Math.max(0, Math.max(x, gate[0])-(size-1));
			this.minY=Math.max(0, Math.max(y, gate[1])-(size-1));
		}

		@Override
		public int compareTo(SmallPoint o) {
			if(this.size==o.size) {
				if(this.minX==o.minX) {
					return Integer.compare(this.minY,o.minY);
				}
				return Integer.compare(this.minX,o.minX);
			}
			return Integer.compare(this.size,o.size);
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		tokens=new StringTokenizer(input.readLine());
		N=Integer.parseInt(tokens.nextToken()); // 크기 
		M=Integer.parseInt(tokens.nextToken()); // 참가자수 
		K=Integer.parseInt(tokens.nextToken()); // 게임시간 
		
		map=new int[N][N];
		peopleMap=new int[N][N][M];
		peopleList=new People[M];
		
		for(int r=0;r<N;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<N;c++) {
				map[r][c]=Integer.parseInt(tokens.nextToken()); 
			}
		}
		
		// 참가자 정보 
		for(int m=0;m<M;m++) {
			tokens=new StringTokenizer(input.readLine());
			int x=Integer.parseInt(tokens.nextToken())-1;
			int y=Integer.parseInt(tokens.nextToken())-1;
			peopleMap[x][y][m]=1; // 1은 존재 
			peopleList[m]=new People(x, y, false);
		}
		
		// 출구 정보 
		tokens=new StringTokenizer(input.readLine());
		int x=Integer.parseInt(tokens.nextToken())-1;
		int y=Integer.parseInt(tokens.nextToken())-1;
		map[x][y]=-1; 
		gate[0]=x;
		gate[1]=y;
		
		for(int k=0;k<K;k++) {
			
			// 이동 
			movePeople();
			
			//참가자 모두 탈출하면 종료 
			if(removeCnt==M) break;
			
			// 회전 
			rotate();
			
		}
		
		output.append(totalmoveCnt+"\n");
		output.append((gate[0]+1)+" "+(gate[1]+1));
		System.out.println(output);
	}
	
	private static void rotate() {
		
		SmallPoint smallPoint=findSmallNemo();
		rotate90(smallPoint);
	}

	private static SmallPoint findSmallNemo() {
		
		PriorityQueue<SmallPoint> pq=new PriorityQueue<>();
		for(int m=0;m<M;m++) {
			// 해당 사람이 제거되었으면 탐색하지 X 
			if(peopleList[m].removed) continue;
			
			pq.add(new SmallPoint(peopleList[m].x, peopleList[m].y, gate));
		}
		
		return pq.poll();
	}

	private static void rotate90(SmallPoint smallPoint) {
		int[][] newMap=new int[N][N];
		int[][][] newPeopleMap=new int[N][N][M];
		
		// 90도 회전
		for(int r=smallPoint.minX;r<smallPoint.minX+smallPoint.size;r++) {
			for(int c=smallPoint.minY;c<smallPoint.minY+smallPoint.size;c++) {
				
				int power=map[smallPoint.size-1-(c-smallPoint.minY)+smallPoint.minX][(r-smallPoint.minX)+smallPoint.minY];
				newMap[r][c]=power>0?power-1:power;
				
				for(int m=0;m<M;m++) {
					if(peopleMap[smallPoint.size-1-(c-smallPoint.minY)+smallPoint.minX][(r-smallPoint.minX)+smallPoint.minY][m]==1) {
						newPeopleMap[r][c][m]=1;
						peopleList[m].x=r;
						peopleList[m].y=c;
					}
				}
			}
		}
		
		// copy
		for(int r=smallPoint.minX;r<smallPoint.minX+smallPoint.size;r++) {
			for(int c=smallPoint.minY;c<smallPoint.minY+smallPoint.size;c++) {
				map[r][c]=newMap[r][c];
				if(map[r][c]==-1) {
					gate[0]=r;
					gate[1]=c;
					
				}
				for(int m=0;m<M;m++) {
					peopleMap[r][c][m]=newPeopleMap[r][c][m];
				}
			}
		}
	}

	private static void movePeople() {
		
		for(int m=0;m<M;m++) {
			// 해당 사람이 제거되었으면 탐색하지 X 
			if(peopleList[m].removed) continue;
			
			PriorityQueue<Info> pq=new PriorityQueue<>();
			
			for(int d=0;d<deltas.length;d++) {
				int nx=peopleList[m].x+deltas[d][0];
				int ny=peopleList[m].y+deltas[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				pq.add(new Info(nx,ny,Math.abs(nx-gate[0])+Math.abs(ny-gate[1])));
			}
			
			Info moveInfo=pq.poll();
			
			// 출구와 거리가 가장 작은 이동 위치 중 벽이 존재하면 거리가 같은 다음 위치로 이동 
			while(pq.size()>0) {
				
				// 움직이는 곳이 벽이면 다음 사람 탐색 
				if(map[moveInfo.x][moveInfo.y]>0&&moveInfo.dist==pq.peek().dist) {
					moveInfo=pq.poll();
				} 
				
				// 현재 위치가 벽이 아니거나 벽이어도 다음 위치의 dist가 작으면 다음 큐 탐색 X 
				else break;
			}
			
			// 벽이면 다음 참가자 탐색 
			if(map[moveInfo.x][moveInfo.y]>0) continue;
			
			// 움직이는 곳이 출구
			if(map[moveInfo.x][moveInfo.y]==-1) {
				peopleList[m].removed=true;
				removeCnt+=1;
				totalmoveCnt+=1;
				peopleMap[peopleList[m].x][peopleList[m].y][m]=0;
			}
			// 단순한 이동
			else {
				// 이동한 곳에 peoplMap을 1로 갱신 
				peopleMap[peopleList[m].x][peopleList[m].y][m]=0;
				peopleList[m].x=moveInfo.x;
				peopleList[m].y=moveInfo.y;
				peopleMap[moveInfo.x][moveInfo.y][m]=1;
				totalmoveCnt+=1;
			}
		}
	}

}