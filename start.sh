
WORKSPACE="papermc"
MC_VERSION="1.16.5"
PAPER_BUILD="731"

if [ ! -d $WORKSPACE ]; then
  mkdir $WORKSPACE
fi

cd $WORKSPACE || exit

PAPER_JAR="paper-$MC_VERSION-$PAPER_BUILD.jar"
PAPER_LNK="https://papermc.io/api/v2/projects/paper/versions/$MC_VERSION/builds/$PAPER_BUILD/downloads/$PAPER_JAR"

if [ ! -f $PAPER_JAR ]; then
  wget -O $PAPER_JAR $PAPER_LNK
fi

java -Xms2G -Xmx2G -jar $PAPER_JAR --nogui
