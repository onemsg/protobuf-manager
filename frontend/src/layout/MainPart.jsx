import { Toolbar } from "@mui/material";
import React from "react";

function MainPart(props) {
  return (
    <main className="main-part">
      <Toolbar variant="dense" />
      <section className="main-part">
        { props.children }
      </section>
    </main>
  );
}

export default MainPart;