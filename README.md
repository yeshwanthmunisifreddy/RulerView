# RulerView


<img src="/RulerView.png" height="700" width = "300">


Step 1. Add the JitPack repository to your build file

    Add it in your root build.gradle at the end of repositories:

    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

    dependencies {
	        implementation 'com.github.yeshwanthmunisifreddy:RulerView:1.0.0'
    }
    

Step 1. Add the JitPack repository to your build file

    <repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
	
Step 2. Add the dependency

	<dependency>
	      <groupId>com.github.yeshwanthmunisifreddy</groupId>
	      <artifactId>RulerView</artifactId>
	      <version>1.0.0</version>
    </dependency>



1.xml

    <technology.nine.rulerview.RulerView
        android:layout_width="match_parent"
        android:layout_height="150dp"
        android:layout_gravity="start"
        android:gravity="center"
        app:alphaEnable="true"
        app:indcatorType="line"
        app:itemSpacing="10dp"
        app:maxLineColor="@color/knight_blue"
        app:maxLineHeight="39dp"
        app:maxLineWidth="3dp"
        app:middleLineColor="#444"
        app:middleLineHeight="28dp"
        app:middleLineWidth="3dp"
        app:minLineColor="#444"
        app:minLineHeight="18dp"
        app:minLineWidth="3dp"
        app:resultTextColor="#444"
        app:resultTextSize="20sp"
        app:scaleTextColor="@color/knight_blue"
        app:scaleTextSize="24sp" />

2.java 

2.1 Option 1

    RulerView rulerview = findViewById(R.id.ruler);
   
    rulerview.initViewParam(78, 20, 180f, 1f);
    rulerview.setChooseValueChangeListener(new RulerView.OnChooseResulterListener() {
        @Override
        public void onChooseValueChange(float value) {
            // TODO do some work
        }
    });
    
    


    <declare-styleable name="RulerView">
        <attr name="defaultSelectedValue" format="float|reference" />
        <attr name="minValue" format="float|reference" />
        <attr name="maxValue" format="float|reference" />
        <attr name="spanValue" format="float|reference" />
        <attr name="minSpanCount" format="float|reference" />
        <attr name="itemSpacing" format="dimension|reference" />
        <attr name="minLineHeight" format="dimension|reference" />
        <attr name="maxLineHeight" format="dimension|reference" />
        <attr name="middleLineHeight" format="dimension|reference" />
        <attr name="minLineWidth" format="dimension|reference" />
        <attr name="maxLineWidth" format="dimension|reference" />
        <attr name="middleLineWidth" format="dimension|reference" />
        <attr name="scaleTextColor" format="color|reference" />
        <attr name="minLineColor" format="color|reference" />
        <attr name="maxLineColor" format="color|reference" />
        <attr name="middleLineColor" format="color|reference" />
        <attr name="scaleTextSize" format="dimension|reference" />
        <attr name="textMarginTop" format="dimension|reference" />
        <attr name="strokeCap" format="enum|reference">
            <enum name="round" value="1" />
            <enum name="butt" value="0" />
            <enum name="square" value="2" />
        </attr>


        <attr name="indcatorColor" format="color|reference" />
        <attr name="indcatorWidth" format="dimension|reference" />
        <attr name="indcatorHeight" format="dimension|reference" />
        <attr name="indcatorType" format="enum|reference">
            <enum name="line" value="1" />
            <enum name="triangle" value="2" />
        </attr>

        <attr name="resultTextColor" format="color|reference" />
        <attr name="unitTextColor" format="color|reference" />
        <attr name="resultTextSize" format="dimension|reference" />
        <attr name="unitTextSize" format="dimension|reference" />
        <attr name="unit" format="string|reference" />
        <attr name="showResult" format="boolean|reference" />
        <attr name="showIndicator" format="boolean|reference" />
        <attr name="showUnit" format="boolean|reference" />
        <attr name="alphaEnable" format="boolean|reference" />
    </declare-styleable>
