class Page extends React.Component{
  scrollTop(){
    window.scrollTo(0, 0);
  }

  componentDidMount(){
    this.scrollTop();
  }
}

export default Page;
