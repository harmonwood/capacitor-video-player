
  Pod::Spec.new do |s|
    s.name = 'CapacitorVideoPlayer'
    s.version = '1.1.3-1'
    s.summary = 'Capacitor Video Player Plugin'
    s.license = 'MIT'
    s.homepage = 'https://github.com/jepiqueau/capacitor-video-player.git'
    s.author = 'Jean Pierre Quéau'
    s.source = { :git => 'https://github.com/jepiqueau/capacitor-video-player.git', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
  end