import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

    static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    static StringBuilder output=new StringBuilder();
    static StringTokenizer tokens;
    static int N, M, maxCnt, moveWarriorDist, stopCnt, attackCnt ;
    static int[] house, park;
    static Warrior[] warriors;
    static int[][] map;
    static boolean[][] see;
    static boolean[][] mapWarrior;
    static int[][] deltas={{-1,0},{1,0},{0,-1},{0,1}};
    static List<int[]> route;
    
    public static class Route {
    	int x, y;
    	List<int[]> route=new ArrayList<int[]>();
    	
		public Route(int x, int y, List<int[]> route, int[] now) {
			super();
			this.x = x;
			this.y = y;
			this.route.addAll(route);
			this.route.add(now);
		}
		public Route(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
    }
    public static class Warrior {
        int x, y;
        boolean isRemoved, isStop;

		public Warrior(int x, int y, boolean isRemoved, boolean isStop) {
			super();
			this.x = x;
			this.y = y;
			this.isRemoved = isRemoved;
			this.isStop = isStop;
		}
    }
    
    public static void main(String[] args) throws IOException {
    	
    	tokens=new StringTokenizer(input.readLine());
    	
    	N=Integer.parseInt(tokens.nextToken());
    	M=Integer.parseInt(tokens.nextToken());
    	
    	house=new int[2];
    	park=new int[2];
    	warriors=new Warrior[M];
    	map=new int[N][N];
    	mapWarrior=new boolean[N][N];
    	route=new ArrayList<int[]>();
    	
    	tokens=new StringTokenizer(input.readLine());
    	house[0]=Integer.parseInt(tokens.nextToken());
    	house[1]=Integer.parseInt(tokens.nextToken());
    	park[0]=Integer.parseInt(tokens.nextToken());
    	park[1]=Integer.parseInt(tokens.nextToken());
    	
    	tokens=new StringTokenizer(input.readLine());
    	for(int m=0;m<M;m++) {
    		int x= Integer.parseInt(tokens.nextToken());
    		int y= Integer.parseInt(tokens.nextToken());
    		warriors[m]=new Warrior(x, y, false, false);
    		mapWarrior[x][y]=true;
    	}
    	
    	for(int r=0;r<N;r++) {
    		tokens=new StringTokenizer(input.readLine());
    		for(int c=0;c<N;c++) map[r][c]=Integer.parseInt(tokens.nextToken());
    	}
    	
    	if(!moveMonster()) {
			output.append(-1);
			System.out.print(output);
			return;
		};
		
		for(int i=0;i<route.size()-1;i++) {
			
			// 이동 
			house[0]=route.get(i)[0];
			house[1]=route.get(i)[1];
			
			// 전사있는 위치 수정 
			mapWarrior=new boolean[N][N];
			for(int m=0;m<M;m++) {
				if(warriors[m].isRemoved) continue;
				if(house[0]==warriors[m].x&&house[1]==warriors[m].y) {
					warriors[m].isRemoved=true;
					continue;
				}
				mapWarrior[warriors[m].x][warriors[m].y]=true;
			}
			
			moveWarriorDist=0;
			stopCnt=0;
			attackCnt=0;
			
			maxCnt=Integer.MIN_VALUE;
			see=new boolean[N][N];
			
			seeUp();
			seeDown();
			seeLeft();
			seeRight();
			
			// 돌로 변환
			for(int m=0;m<M;m++) {
				if(warriors[m].isRemoved) continue;
				if(see[warriors[m].x][warriors[m].y]) {
					stopCnt++;
					warriors[m].isStop=true; // 돌로 변환 
				}
			}
			
//			for(int r=0;r<N;r++) System.out.println(Arrays.toString(see[r]));
//			System.out.println();
			
			// 모든 전사가 이동한 거리의 합, 메두사로 인해 돌이 된 전사의 수, 메두사를 공격한 전사의 수
			moveWarrior();
			
			output.append(moveWarriorDist+" "+stopCnt+" "+attackCnt+"\n");
		}
		output.append(0);
		System.out.print(output);
    }

	private static void moveWarrior() {
		
		for(int m=0;m<M;m++) {
			
			// 진 전사는 pass 
			if(warriors[m].isRemoved) continue;
			
			// 돌이 된 전사는 pass 
			if(warriors[m].isStop) {
				warriors[m].isStop=false;
				continue;
			}
						
			int[] newWarriorPoint=new int[] {warriors[m].x, warriors[m].y};
			
			int dist=Math.abs(house[0]-newWarriorPoint[0])+Math.abs(house[1]-newWarriorPoint[1]);
			
			for(int d=0;d<4;d++) {
				int nx=newWarriorPoint[0]+deltas[d][0];
				int ny=newWarriorPoint[1]+deltas[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				if(see[nx][ny]) continue;
				
				int newDist=Math.abs(house[0]-nx)+Math.abs(house[1]-ny);
				
				if(dist>newDist) {
					newWarriorPoint[0]=nx;
					newWarriorPoint[1]=ny;
					moveWarriorDist++;
					break;
				}
			}
			
			int[][] deltas2={{0,-1},{0,1},{-1,0},{1,0}};
			
			dist=Math.abs(house[0]-newWarriorPoint[0])+Math.abs(house[1]-newWarriorPoint[1]);
			
			for(int d=0;d<4;d++) {
				int nx=newWarriorPoint[0]+deltas2[d][0];
				int ny=newWarriorPoint[1]+deltas2[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				if(see[nx][ny]) continue;
				
				int newDist=Math.abs(house[0]-nx)+Math.abs(house[1]-ny);
				
				if(dist>newDist) {
					newWarriorPoint[0]=nx;
					newWarriorPoint[1]=ny;
					moveWarriorDist++;
					break;
				}
			}
			
			warriors[m].x=newWarriorPoint[0];
			warriors[m].y=newWarriorPoint[1];
			
			// 메두사 공격
			if(warriors[m].x==house[0]&&warriors[m].y==house[1]) {
				warriors[m].isRemoved=true;
				attackCnt++;
			}
		}
		
	}

	private static void seeRight() {
		int startX;
		int endX;
		int x=house[0]+deltas[3][0];
		int y=house[1]+deltas[3][1];
		int size=3;
		int cnt=0;
		boolean[][] seeDown=new boolean[N][N];
		
		while(y<N) {
			startX=x-(size/2)<0?0:x-(size/2);
			endX=x+(size/2)>=N?N-1:x+(size/2);
			
			for(int nx=startX;nx<=endX;nx++) {
				if(nx>=x-1&&nx<=x+1) {
					if(size==3) seeDown[nx][y]=true;
					else if(mapWarrior[nx][y-1]||!seeDown[nx][y-1]) seeDown[nx][y]=false;
					else seeDown[nx][y]=true;
				} else if(nx<x-1) {
					if(mapWarrior[nx][y-1]||mapWarrior[nx+1][y-1]||!seeDown[nx][y-1]||!seeDown[nx+1][y-1]) seeDown[nx][y]=false;
					else seeDown[nx][y]=true;
				} else if(nx>x+1) {
					if(mapWarrior[nx][y-1]||mapWarrior[nx-1][y-1]||!seeDown[nx][y-1]||!seeDown[nx-1][y-1]) seeDown[nx][y]=false;
					else seeDown[nx][y]=true;
				}
			}
			
			x+=deltas[3][0];
			y+=deltas[3][1];
			size+=2;
		}
		
		for(int m=0;m<M;m++) {
			if(warriors[m].isRemoved) continue;
			if(seeDown[warriors[m].x][warriors[m].y]) {
				cnt++;
			}
		}
		
		if(cnt>maxCnt) {
			for(int r=0;r<N;r++) {
				for(int c=0;c<N;c++) see[r][c]=seeDown[r][c];
			}
			maxCnt=cnt;
		}
	}

	private static void seeLeft() {
		
		int startX;
		int endX;
		int x=house[0]+deltas[2][0];
		int y=house[1]+deltas[2][1];
		int size=3;
		int cnt=0;
		boolean[][] seeDown=new boolean[N][N];
		
		while(y>=0) {
			startX=x-(size/2)<0?0:x-(size/2);
			endX=x+(size/2)>=N?N-1:x+(size/2);
			
			for(int nx=startX;nx<=endX;nx++) {
				if(nx>=x-1&&nx<=x+1) {
					if(size==3) seeDown[nx][y]=true;
					else if(mapWarrior[nx][y+1]||!seeDown[nx][y+1]) seeDown[nx][y]=false;
					else seeDown[nx][y]=true;
				} else if(nx<x-1) {
					if(mapWarrior[nx][y+1]||mapWarrior[nx+1][y+1]||!seeDown[nx][y+1]||!seeDown[nx+1][y+1]) seeDown[nx][y]=false;
					else seeDown[nx][y]=true;
				} else if(nx>x+1) {
					if(mapWarrior[nx][y+1]||mapWarrior[nx-1][y+1]||!seeDown[nx][y+1]||!seeDown[nx-1][y+1]) seeDown[nx][y]=false;
					else seeDown[nx][y]=true;
				}
			}
			
			x+=deltas[2][0];
			y+=deltas[2][1];
			size+=2;
		}
		
		for(int m=0;m<M;m++) {
			if(warriors[m].isRemoved) continue;
			if(seeDown[warriors[m].x][warriors[m].y]) {
				cnt++;
			}
		}
		
		if(cnt>maxCnt) {
			for(int r=0;r<N;r++) {
				for(int c=0;c<N;c++) see[r][c]=seeDown[r][c];
			}
			maxCnt=cnt;
		}	
	}

	private static void seeUp() {
		int startY;
		int endY;
		int x=house[0]+deltas[0][0];
		int y=house[1]+deltas[0][1];
		int size=3;
		int cnt=0;
		boolean[][] seeDown=new boolean[N][N];
		
		while(x>=0) {
			startY=y-(size/2)<0?0:y-(size/2);
			endY=y+(size/2)>=N?N-1:y+(size/2);
			
			for(int ny=startY;ny<=endY;ny++) {
				if(ny>=y-1&&ny<=y+1) {
					if(size==3) seeDown[x][ny]=true;
					else if(mapWarrior[x+1][ny]||!seeDown[x+1][ny]) seeDown[x][ny]=false;
					else seeDown[x][ny]=true;
				} else if(ny<y-1) {
					if(mapWarrior[x+1][ny]||mapWarrior[x+1][ny+1]||!seeDown[x+1][y]||!seeDown[x+1][ny+1]) seeDown[x][ny]=false;
					else seeDown[x][ny]=true;
				} else if(ny>y+1) {
					if(mapWarrior[x+1][ny]||mapWarrior[x+1][ny-1]||!seeDown[x+1][y]||!seeDown[x+1][ny-1]) seeDown[x][ny]=false;
					else seeDown[x][ny]=true;
				}
			}
			
			x+=deltas[0][0];
			y+=deltas[0][1];
			size+=2;
		}
		
		for(int m=0;m<M;m++) {
			if(warriors[m].isRemoved) continue;
			if(seeDown[warriors[m].x][warriors[m].y]) {
				cnt++;
			}
		}
		
		if(cnt>maxCnt) {
			for(int r=0;r<N;r++) {
				for(int c=0;c<N;c++) see[r][c]=seeDown[r][c];
			}
			maxCnt=cnt;
		}
	}

	private static void seeDown() {
		
		int startY;
		int endY;
		int x=house[0]+deltas[1][0];
		int y=house[1]+deltas[1][1];
		int size=3;
		int cnt=0;
		boolean[][] seeDown=new boolean[N][N];
		
		while(x<N) {
			startY=y-(size/2)<0?0:y-(size/2);
			endY=y+(size/2)>=N?N-1:y+(size/2);
			
			for(int ny=startY;ny<=endY;ny++) {
				if(ny>=y-1&&ny<=y+1) {
					if(size==3) seeDown[x][ny]=true;
					else if(mapWarrior[x-1][ny]||!seeDown[x-1][ny]) seeDown[x][ny]=false;
					else seeDown[x][ny]=true;
				} else if(ny<y-1) {
					if(mapWarrior[x-1][ny]||mapWarrior[x-1][ny+1]||!seeDown[x-1][y]||!seeDown[x-1][ny+1]) seeDown[x][ny]=false;
					else seeDown[x][ny]=true;
				} else if(ny>y+1) {
					if(mapWarrior[x-1][ny]||mapWarrior[x-1][ny-1]||!seeDown[x-1][y]||!seeDown[x-1][ny-1]) seeDown[x][ny]=false;
					else seeDown[x][ny]=true;
				}
			}
			
			x+=deltas[1][0];
			y+=deltas[1][1];
			size+=2;
		}
		
		for(int m=0;m<M;m++) {
			if(warriors[m].isRemoved) continue;
			if(seeDown[warriors[m].x][warriors[m].y]) {
				cnt++;
			}
		}
		
		if(cnt>maxCnt) {
			for(int r=0;r<N;r++) {
				for(int c=0;c<N;c++) see[r][c]=seeDown[r][c];
			}
			maxCnt=cnt;
		}
	}

	private static boolean moveMonster() {
		
		Queue<Route> q=new LinkedList<>();
		q.add(new Route(house[0], house[1]));
		boolean[][] visited=new boolean[N][N];
		
		while(q.size()>0) {
			Route now=q.poll();
			
			if(now.x==park[0]&&now.y==park[1]) {
				route=now.route;
				return true;
			}
			
			for(int d=0;d<4;d++) {
				
				int nx=now.x+deltas[d][0];
				int ny=now.y+deltas[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				if(map[nx][ny]==1||visited[nx][ny]) continue;
				
				q.add(new Route(nx,ny,now.route,new int[] {nx,ny}));
				visited[nx][ny]=true;
			}
		}
		return false;
	}
}
