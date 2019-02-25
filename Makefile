build:
	javac $(shell find src -name *.java) -d output/
	cp -r resources output/
	cp src/*.txt src/*.pacman output/

run: build
	java -cp output ru.pacman.GameLoader

clean:
	rm -rf output/	