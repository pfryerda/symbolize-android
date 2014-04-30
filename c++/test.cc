#include <iostream>
#include "level.h"
using namespace std;

int main() {
	Graph puzzle;
	puzzle.push_back(new Line(make_pair(10, 10), make_pair(50, 50)));
	puzzle.push_back(new Line(make_pair(50, 50), make_pair(10, 90)));
	vector<Graph> solns;
	Graph soln;
	soln.push_back(new Line(make_pair(10, 10), make_pair(50, 50)));
	soln.push_back(new Line(make_pair(50, 50), make_pair(10, 90)));
	soln.push_back(new Line(make_pair(50, 50), make_pair(90, 90)));
	solns.push_back(soln);
	Level level(puzzle, solns, 1, 0);

	Graph guess;
	guess.push_back(new Line(make_pair(10, 10), make_pair(50, 50)));
	guess.push_back(new Line(make_pair(50, 50), make_pair(10, 90)));
	guess.push_back(new Line(make_pair(30, 50), make_pair(90, 90)));
	cout << level.checkSolution(guess) << endl;
}