import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder output=new StringBuilder();
	static StringTokenizer tokens;
	static int R, C, K, ans;
	static int[][] map, deltas= {{-1,0},{0,1},{1,0},{0,-1}};
	static Monster monster;
	
	static class Monster {
		int x, y, dir;

		public Monster(int x, int y, int dir) {
			super();
			this.x = x;
			this.y = y;
			this.dir = dir;
		}
	}
	public static void main(String[] args) throws IOException {
		tokens=new StringTokenizer(input.readLine());
		R=Integer.parseInt(tokens.nextToken());
		C=Integer.parseInt(tokens.nextToken());
		K=Integer.parseInt(tokens.nextToken());
		
		map=new int[R][C];
		ans=0;
				
		for(int k=1;k<=K;k++) {
			tokens=new StringTokenizer(input.readLine());
			int c=Integer.parseInt(tokens.nextToken())-1;
			int d=Integer.parseInt(tokens.nextToken());
			monster=new Monster(-2, c, d);
			
			// 골렘 이동 
			while(true) {
				// 남 이동 
				if(check(monster.x+deltas[2][0],monster.y+deltas[2][1],new int[] {1,2,3})) {
					monster.x+=deltas[2][0];
					monster.y+=deltas[2][1];
					continue;
				} 
				// 서 이동 
				else if(check(monster.x+deltas[3][0],monster.y+deltas[3][1],new int[] {0,3,2})
						&&check(monster.x+deltas[3][0]+deltas[2][0],monster.y+deltas[3][1]+deltas[2][1],new int[] {1,2,3})) {
					monster.x+=deltas[3][0]+deltas[2][0];
					monster.y+=deltas[3][1]+deltas[2][1];
					monster.dir=monster.dir-1<0?3:monster.dir-1;
					continue;
				} 
				// 동 이동 
				else if(check(monster.x+deltas[1][0],monster.y+deltas[1][1],new int[] {0,1,2})
						&&check(monster.x+deltas[1][0]+deltas[2][0],monster.y+deltas[1][1]+deltas[2][1],new int[] {1,2,3})) {
					monster.x+=deltas[1][0]+deltas[2][0];
					monster.y+=deltas[1][1]+deltas[2][1];
					monster.dir=(monster.dir+1)%4;
					continue;
				} 
				break;
			}
			
			// 골렘이 격자 밖이면 
			if(monster.x<=0) {
				map=new int[R][C];
				continue;
			}
			
			// 골렘을 map에 셋팅 
			setMap(k);
			
//			for(int r=0;r<R;r++) System.out.println(Arrays.toString(map[r]));
//			System.out.println();
			
			// 정령 이동
			moveGod();
		}
		
		output.append(ans);
		System.out.println(ans);
	}
	private static void moveGod() {
		
		Queue<int[]> q=new LinkedList<>();
		boolean[][] visited=new boolean[R][C];
		q.add(new int[] {monster.x, monster.y});
		visited[monster.x][monster.y]=true;
		int maxRow=monster.x;
		
		while(q.size()>0) {
			int[] now=q.poll();
			
			if(maxRow<now[0]) {
				maxRow=now[0];
			}
			for(int d=0;d<4;d++) {
				int nx=now[0]+deltas[d][0];
				int ny=now[1]+deltas[d][1];
				
				if(nx<0||nx>=R||ny<0||ny>=C) continue;
				
				if(map[now[0]][now[1]]>0&&Math.abs(map[now[0]][now[1]])==Math.abs(map[nx][ny])&&!visited[nx][ny]) {
					q.add(new int[] {nx,ny});
					visited[nx][ny]=true;
				} else if(map[now[0]][now[1]]<0&&!visited[nx][ny]&&map[nx][ny]!=0){
					q.add(new int[] {nx,ny});
					visited[nx][ny]=true;
				}
			}
		}
		
		ans+=maxRow+1;
//		System.out.println(maxRow+1);
	}
	private static void setMap(int num) {
		
		map[monster.x][monster.y]=num;
		for(int d=0;d<4;d++) {
			int nx=monster.x+deltas[d][0];
			int ny=monster.y+deltas[d][1];
			
			if(nx<0||nx>=R||ny<0||ny>=C) continue;
			
			if(monster.dir==d) map[nx][ny]=num*(-1);
			else map[nx][ny]=num;
		}
	}
	private static boolean check(int x, int y, int[] checkDir) {
		
		for(int dir : checkDir) {
			int nx=x+deltas[dir][0];
			int ny=y+deltas[dir][1];
			
			if(nx<0) continue;
			if(nx>=R||ny<0||ny>=C) return false;
			
			if(Math.abs(map[nx][ny])>0) return false;
		}
		return true;
	}
}
