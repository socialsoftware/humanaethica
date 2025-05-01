FROM ubuntu:22.04

ENV TZ=Europe/Lisbon
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    apt update -y && apt upgrade -y && \
    apt install -y --no-install-recommends openjdk-21-jdk postgresql postgresql-client maven gawk wget python2 sudo git && \
    apt install -y --no-install-recommends libgtk2.0-0 libgtk-3-0 libgbm-dev libnotify-dev libgconf-2-4 libnss3 libxss1 libasound2 libxtst6 xauth xvfb && \
    apt install -y git && \
    wget -O - https://deb.nodesource.com/setup_21.x | bash - && apt install -y nodejs && \
    /etc/init.d/postgresql start && \
    sudo -u postgres psql -c "ALTER USER postgres PASSWORD 'postgres';" && \
    sudo -u postgres psql -c "CREATE DATABASE hedb;" && \
    wget -O /bin/cover2cover.py https://raw.githubusercontent.com/rix0rrr/cover2cover/master/cover2cover.py && \
    apt autoremove -y && apt-get autoclean && apt clean