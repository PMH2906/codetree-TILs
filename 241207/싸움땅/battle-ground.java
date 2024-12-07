import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

// N*N
// 능력치
public class Main {
	
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder output=new StringBuilder();
	static StringTokenizer tokens;
	static int N, M, K;
	static PriorityQueue<Integer>[][] gun;
	static People[] peoples;
	static int[][] map, deltas= {{-1,0},{0,1},{1,0},{0,-1}}; // 상우하좌 
	
	public static class People {
		int x, y, dir, s, gun, point;

		public People(int x, int y, int dir, int s) {
			super();
			this.x = x;
			this.y = y;
			this.dir = dir;
			this.s = s;
		}
	}

	public static void main(String[] args) throws IOException {
		
		tokens=new StringTokenizer(input.readLine());
		N=Integer.parseInt(tokens.nextToken());
		M=Integer.parseInt(tokens.nextToken());
		K=Integer.parseInt(tokens.nextToken());
		
		gun=new PriorityQueue[N][N];
		for(int r=0;r<N;r++) {
			for(int c=0;c<N;c++) {
				gun[r][c]=new PriorityQueue<>(new Comparator<Integer>() {
					@Override
					public int compare(Integer o1, Integer o2) {
						// TODO Auto-generated method stub
						return Integer.compare(o1, o2)*-1;
					}
				});
			}
		}
		peoples=new People[M+1];
		map=new int[N][N];
		
		for(int r=0;r<N;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<N;c++) {
				int g=Integer.parseInt(tokens.nextToken());
				if(g>0) gun[r][c].add(g);
			}
		}
		
		for(int m=1;m<=M;m++) {
			tokens=new StringTokenizer(input.readLine());
			int x=Integer.parseInt(tokens.nextToken())-1;
			int y=Integer.parseInt(tokens.nextToken())-1;
			int d=Integer.parseInt(tokens.nextToken());
			int s=Integer.parseInt(tokens.nextToken());
			peoples[m]=new People(x, y, d, s);
			map[x][y]=m;
		}
		
		for(int k=0;k<K;k++) {
			move();
		}
		
		// K라운드동안 획득한 포인트 
		for(int m=1;m<=M;m++) {
			output.append(peoples[m].point+" ");
		}
		
		System.out.println(output);
	}

	//
	// 1. 방향대로 움직이고, 벽일 경우 반대 방향으로 +1 움직임 
	// 2. 플레이어 X, 총 획득
	private static void move() {
		
		for(int m=1;m<=M;m++) {
			int nx=peoples[m].x+deltas[peoples[m].dir][0];
			int ny=peoples[m].y+deltas[peoples[m].dir][1];
			
			if(nx<0||nx>=N||ny<0||ny>=N) {
				// 반대 방향으로 움직이기 
				peoples[m].dir=(peoples[m].dir+2)%4;
				nx=peoples[m].x+deltas[peoples[m].dir][0];
				ny=peoples[m].y+deltas[peoples[m].dir][1];
			}
			
			// map에 플레이어 번호 삭제 
			map[peoples[m].x][peoples[m].y]=0;
			// 플레이어 정보 업데이트 
			peoples[m].x=nx;peoples[m].y=ny;
			
			// 플레이어 있을 경우 
			if(map[nx][ny]>0) {
				fight(m, peoples[m], map[nx][ny], peoples[map[nx][ny]]);
				continue;
			} 
			
			// map에 플레이어 번호 업데이트 
			map[peoples[m].x][peoples[m].y]=m;
			
			// 플레이어가 없을 경우 
			// 플레이어에게 총이 있을경우, 내려놓기 
			if(peoples[m].gun!=0) gun[nx][ny].add(peoples[m].gun);
			
			// 자리에 총이 있을경우, 가장 큰 총 획득 
			if(gun[nx][ny].size()>0) peoples[m].gun=gun[nx][ny].poll();
			
//			for(int r=0;r<N;r++) System.out.println(Arrays.toString(map[r]));
//			System.out.println();
		}
		
	}

	// 해당 플레이어의 초기 능력치와 가지고 있는 총의 공격력의 합을 비교하여 더 큰 플레이어가 이기게 됩니다. 
	// 만일 이 수치가 같은 경우에는 플레이어의 초기 능력치가 높은 플레이어가 승리하게 됩니다. 
	// 이긴 플레이어는 각 플레이어의 초기 능력치와 가지고 있는 총의 공격력의 합의 차이만큼을 포인트로 획득하게 됩니다.
	// 진플레이
		// 총 내려놓고 원래 가지고 있던 방향대로 이동, 
		// 벽 혹은 플레이어가 있을 경우 오른쪽으로 90도씩 회전하고 없는 칸으로 이동 
		// 총 있으면 획득 
		
		// 이긴 플레이
		// 그 자리에 높은 총 획득 
	private static void fight(int player1Num, People player1, int player2Num, People player2) {
		
		People win;
		People lose;
		int winNum;
		int loseNum;
		if(player1.gun+player1.s==player2.gun+player2.s) {
			if(player1.s>player2.s) {
				win=player1;
				winNum=player1Num;
				lose=player2;
				loseNum=player2Num;
			} else {
				win=player2;
				winNum=player2Num;
				lose=player1;
				loseNum=player1Num;
			}
		} else if(player1.gun+player1.s>player2.gun+player2.s){
			win=player1;
			winNum=player1Num;
			lose=player2;
			loseNum=player2Num;
		} else {
			win=player2;
			winNum=player2Num;
			lose=player1;
			loseNum=player1Num;
		}
		win.point+=Math.abs((player1.gun+player1.s)-(player2.gun+player2.s));
		
		// 진 플레이가 총이 있을 경우, 내려놓기 
		if(lose.gun!=0) {
			gun[lose.x][lose.y].add(lose.gun);
			lose.gun=0;
		}
			
		int nx=lose.x+deltas[lose.dir][0];
		int ny=lose.y+deltas[lose.dir][1];
		
		while((nx<0||nx>=N||ny<0||ny>=N)||map[nx][ny]>0) {
			lose.dir=(lose.dir+1)%4;
			nx=lose.x+deltas[lose.dir][0];
			ny=lose.y+deltas[lose.dir][1];
		}
		// 진 플레이어 이동 
		lose.x=nx; lose.y=ny;
		map[lose.x][lose.y]=loseNum;
		
		// 자리에 총이 있을경우, 가장 큰 총 획득 
		if(gun[lose.x][lose.y].size()>0) lose.gun=gun[lose.x][lose.y].poll();
		
		// 이긴 플레이가 총이 있을경우, 내려놓기 
		if(win.gun!=0) gun[win.x][win.y].add(win.gun);
		// 자리에 총이 있을경우, 가장 큰 총 획득 
		if(gun[win.x][win.y].size()>0)	win.gun=gun[win.x][win.y].poll();
		map[win.x][win.y]=winNum;
	}

}
