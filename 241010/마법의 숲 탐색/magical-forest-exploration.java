import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.management.MonitorInfo;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringTokenizer tokens;
	static StringBuilder output=new StringBuilder();
	static int R, C, K, ans;
	static int[][] map, deltas= {{-1,0},{0,1},{1,0},{0,-1}};
	static Monster monster;
	
	public static class Monster {
		int x, y, d;

		public Monster(int x, int y, int d) {
			super();
			this.x = x;
			this.y = y;
			this.d = d;
		}
	}

	public static void main(String[] args) throws IOException {
		
		tokens=new StringTokenizer(input.readLine());
		R=Integer.parseInt(tokens.nextToken());
		C=Integer.parseInt(tokens.nextToken());
		K=Integer.parseInt(tokens.nextToken());
		
		map=new int[R][C];
		
		for(int k=0;k<K;k++) {
			tokens=new StringTokenizer(input.readLine());
			int c=Integer.parseInt(tokens.nextToken())-1;
			int d=Integer.parseInt(tokens.nextToken());
			
			monster=new Monster(-2, c, d);
			
			moveMonster();
			
			// 골렘이 밖에 있으면 map 초기화 
			if(!isInner()) {
				map=new int[R][C];
				continue;
			}
			
			settingMap();
			moveGod();
			
//			System.out.println(k+"턴 "+ans);
//			for(int r=0;r<R;r++) {
//				System.out.println(Arrays.toString(map[r]));
//			}
		}
		System.out.println(ans);

	}

	private static void settingMap() {
		
		// 센터 : 3, 출구 : 2, 그 외 : 1
		map[monster.x][monster.y]=3;
		for(int d=0;d<deltas.length;d++) {
			int nx=monster.x+deltas[d][0];
			int ny=monster.y+deltas[d][1];
			
			if(monster.d==d) map[nx][ny]=2;
			else map[nx][ny]=1;
		}
	}

	private static void moveGod() {
		
		Queue<int[]> q=new LinkedList<>();
		boolean visited[][]=new boolean[R][C];
		int maxRow=Integer.MIN_VALUE;
		
		q.add(new int[] {monster.x, monster.y});
		visited[monster.x][monster.y]=true;
		
		while(q.size()>0) {
			
			int[] now=q.poll();
			if(maxRow<now[0]) maxRow=now[0];
			
			for(int d=0;d<deltas.length;d++) {
				int nx=now[0]+deltas[d][0];
				int ny=now[1]+deltas[d][1];
				
				if(nx<0||nx>=R||ny<0||ny>=C) continue;
				
				if(map[now[0]][now[1]]==1&&!visited[nx][ny]&&map[nx][ny]==3) {
					q.add(new int[] {nx,ny});
					visited[nx][ny]=true;
					
				} else if(map[now[0]][now[1]]!=1&&!visited[nx][ny]&&map[nx][ny]!=0) {
					q.add(new int[] {nx,ny});
					visited[nx][ny]=true;
				} 
			}
		}
		ans+=maxRow+1;
	}

	private static boolean isInner() {
		if(monster.x>=1) return true;
		return false;
	}

	private static void moveMonster() {
		
	    boolean check=true;
		while(check) {
			check=false;
			if(isMoved(monster.x+deltas[2][0], monster.y+deltas[2][1], 2)) {
				check=true;
				// 남으로 이동 
				monster.x+=deltas[2][0];
				monster.y+=deltas[2][1];
			} else if(isMoved(monster.x+deltas[3][0], monster.y+deltas[3][1],3)&&isMoved(monster.x+deltas[3][0]+deltas[2][0], monster.y+deltas[3][1]+deltas[2][1], 2)) {
				check=true;
				// 서&남으로 이동 
				monster.x+=deltas[3][0]+deltas[2][0];
				monster.y+=deltas[3][1]+deltas[2][1];
				monster.d=(monster.d-1)<0?3:monster.d-1;
			} else if(isMoved(monster.x+deltas[1][0], monster.y+deltas[1][1],1)&&isMoved(monster.x+deltas[1][0]+deltas[2][0], monster.y+deltas[1][1]+deltas[2][1], 2)) {
				check=true;
				// 동&남 이동 
				monster.x+=deltas[1][0]+deltas[2][0];
				monster.y+=deltas[1][1]+deltas[2][1];
				monster.d=(monster.d+1)%4;
			}
		}
	}

	private static boolean isMoved(int x, int y, int direct) {
		
		for(int d=0;d<deltas.length;d++) {
			if(direct==2&&d==0) continue;
			if(direct==3&&d==1) continue;
			if(direct==1&&d==3) continue;
			
			int nx=x+deltas[d][0];
			int ny=y+deltas[d][1];
			
			if(nx>=R||ny<0||ny>=C) return false;
			if(nx<0) continue;
			
			if(map[nx][ny]!=0) return false;
		}
		
		return true;
	}

}